package com.solux.bodybubby.domain.post.service;

import com.solux.bodybubby.domain.post.dto.request.PostRequestDto;
import com.solux.bodybubby.domain.post.dto.response.PostResponseDto;
import com.solux.bodybubby.domain.post.entity.Post;
import com.solux.bodybubby.domain.post.repository.PostRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepositoryTemp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepositoryTemp userRepository; // 임시 레포. 추후 변경 예정

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

        return postRepository.save(post).getId();
    }

    // 게시글 전체 조회
    public Page<PostResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        return posts.map(PostResponseDto::fromEntity);
    }

    // 게시글 상세 조회
    @Transactional
    public PostResponseDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 조회수 증가 로직 필요

        return PostResponseDto.fromEntity(post);
    }

    // 게시글 수정
    @Transactional
    public void updatePost(Long postId, PostRequestDto dto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 유저 체크 로직 수정 필요
        if(!post.getId().equals(1L)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        post.update(dto.getTitle(), dto.getContent(), dto.getVisibility());
    }

    // 게시글 삭제(soft delete)
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 유저 체크 로직 수정 필요
        if(!post.getId().equals(1L)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        postRepository.delete(post);
    }



}
