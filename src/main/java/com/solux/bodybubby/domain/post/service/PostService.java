package com.solux.bodybubby.domain.post.service;

import com.solux.bodybubby.domain.post.dto.request.PostRequestDto;
import com.solux.bodybubby.domain.post.dto.response.PostResponseDto;
import com.solux.bodybubby.domain.post.entity.Hashtag;
import com.solux.bodybubby.domain.post.entity.Post;
import com.solux.bodybubby.domain.post.entity.PostHashtag;
import com.solux.bodybubby.domain.post.repository.HashtagRepository;
import com.solux.bodybubby.domain.post.repository.PostHashtagRepository;
import com.solux.bodybubby.domain.post.repository.PostLikeRepository;
import com.solux.bodybubby.domain.post.repository.PostRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import com.solux.bodybubby.global.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final HashtagRepository hashtagRepository;
    private final PostLikeRepository postLikeRepository;
    private final S3Service s3Service;

    // 게시글 생성
    @Transactional
    public Long createPost(PostRequestDto dto, MultipartFile image, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이미지 S3에 업로드
        String uploadedUrl = null;
        if (image != null && !image.isEmpty())
            uploadedUrl = s3Service.uploadFile(image);

        Post post = Post.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .imageUrl(uploadedUrl)
                .visibility(dto.getVisibility())
                .likeCount(0)
                .build();

        postRepository.save(post);

        if (dto.getHashtags() != null) {
            savePostHashtags(post, dto.getHashtags());
        }
        
        return post.getId();
    }

    private void savePostHashtags(Post post, List<String> tagNames) {
        for (String tagName : tagNames) {
            Hashtag hashtag = hashtagRepository.findByTagName(tagName)
                    .orElseGet(() -> hashtagRepository.save(
                            Hashtag.builder().tagName(tagName).build()
                    ));

            postHashtagRepository.save(
                    PostHashtag.builder()
                            .post(post)
                            .hashtag(hashtag)
                            .build()
            );
        }
    }

    // 게시글 전체 조회
    @Transactional
    public Page<PostResponseDto> getAllPosts(Long currentUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return convertToDtoPags(posts, currentUserId);
    }

    // 게시글 상세 조회
    @Transactional
    public PostResponseDto getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 조회수 증가 로직 필요

        boolean isLiked = postLikeRepository.existsByPostAndUser(post,
                userRepository.getReferenceById(currentUserId));

        return PostResponseDto.fromEntity(post, isLiked);
    }

    // 게시글 수정
    @Transactional
    public Long updatePost(Long postId, PostRequestDto dto, MultipartFile image, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 유저 체크 로직 수정 필요
//        if(!post.getUser().getId().equals(currentUserId)) {
        if(!post.getId().equals(1L)) {
            throw new BusinessException(ErrorCode.UPDATE_PERMISSION_DENIED);
        }

        if (image != null && !image.isEmpty()) {
            String newUrl = s3Service.uploadFile(image);
            post.updateImageUrl(newUrl);
        } else if (Boolean.TRUE.equals(dto.getImageDeleted())) {
            // 만약 프론트에서 사진 삭제 버튼을 눌렀다면 null 처리
            post.updateImageUrl(null);
        }

        
    post.update(dto.getTitle(), dto.getContent(), dto.getVisibility(), null);

        updatePostHashtags(post, dto.getHashtags());

        return post.getId();
    }

    private void updatePostHashtags(Post post, List<String> tagNames) {
        postHashtagRepository.deleteByPostId(post.getId());

        if (tagNames == null || tagNames.isEmpty()) return;

        for (String name : tagNames) {
            Hashtag hashtag = hashtagRepository.findByTagName(name)
                    .orElseGet(() -> hashtagRepository.save(
                            Hashtag.builder().tagName(name).build()
                    ));

            postHashtagRepository.save(
                    PostHashtag.builder()
                            .post(post)
                            .hashtag(hashtag)
                            .build()
            );
        }
    }

    // 게시글 삭제(soft delete)
    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if(!post.getUser().getId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.DELETE_PERMISSION_DENIED);
        }

        postRepository.delete(post);
    }

    // 해시태그로 필터링 조회
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByHashtag(String tagName, Long currentUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByHashtagName(tagName, currentUserId, pageable);

        return posts.map(post -> {
            boolean isLiked = postLikeRepository.existsByPostAndUser(post,
                    userRepository.getReferenceById(currentUserId));
            return PostResponseDto.fromEntity(post, isLiked);
        });
    }

    // 게시글 키워드 검색
    @Transactional(readOnly = true)
    public  Page<PostResponseDto> getPostsByKeyword(String keyword, Long currentUserId, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByKeyword(keyword, currentUserId, pageable);
        return convertToDtoPags(posts, currentUserId);
    }

    private Page<PostResponseDto> convertToDtoPags(Page<Post> posts, Long currentUserId) {
        return posts.map(post -> {
            boolean isLiked = postLikeRepository.findByPostAndUser(post,
                    userRepository.findById(currentUserId).get()).isPresent();
            return PostResponseDto.fromEntity(post, isLiked);
        });
    }

}
