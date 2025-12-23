package com.solux.bodybubby.domain.post.service;

import com.solux.bodybubby.domain.post.dto.request.PostRequestDto;
import com.solux.bodybubby.domain.post.dto.response.PostResponseDto;
import com.solux.bodybubby.domain.post.entity.Hashtag;
import com.solux.bodybubby.domain.post.entity.Post;
import com.solux.bodybubby.domain.post.entity.PostHashtag;
import com.solux.bodybubby.domain.post.repository.HashtagRepository;
import com.solux.bodybubby.domain.post.repository.PostHashtagRepository;
import com.solux.bodybubby.domain.post.repository.PostRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepositoryTemp;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepositoryTemp userRepository; // 임시 레포. 추후 변경 예정
    private final PostHashtagRepository postHashtagRepository;
    private final HashtagRepository hashtagRepository;

    // 게시글 생성
    @Transactional
    public Long createPost(PostRequestDto dto) {
        // 임시로 1번 유저를 작성자로 지정
        User tempUser = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("테스트용 유저(ID:1)가 DB에 없습니다."));

        Post post = Post.builder()
                .user(tempUser)
                .title(dto.getTitle())
                .content(dto.getContent())
                .visibility(dto.getVisibility())
                .viewCount(0)
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
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    // 게시글 상세 조회
    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 조회수 증가 로직 필요

        return PostResponseDto.fromEntity(post);
    }

    // 게시글 수정
    @Transactional
    public Long updatePost(Long postId, PostRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 유저 체크 로직 수정 필요
        if(!post.getId().equals(1L)) {
            throw new BusinessException(ErrorCode.UPDATE_PERMISSION_DENIED);
        }

        post.update(dto.getTitle(), dto.getContent(), dto.getVisibility());

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
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 유저 체크 로직 수정 필요
        if(!post.getId().equals(1L)) {
            throw new BusinessException(ErrorCode.DELETE_PERMISSION_DENIED);
        }

        postRepository.delete(post);
    }

    // 해시태그로 필터링 조회
    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPostsByHashtag(String tagName, Pageable pageable) {
        Page<Post> posts = postRepository.findAllByHashtagName(tagName, pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

}
