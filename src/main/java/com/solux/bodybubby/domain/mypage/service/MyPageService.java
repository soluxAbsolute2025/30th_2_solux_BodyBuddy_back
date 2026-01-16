package com.solux.bodybubby.domain.mypage.service;

import com.solux.bodybubby.domain.mypage.dto.MyPageResponseDto;
import com.solux.bodybubby.domain.mypage.dto.MyPostDto;
import com.solux.bodybubby.domain.mypage.dto.PrivacySettingsDto;
import com.solux.bodybubby.domain.mypage.entity.LevelTier;
import com.solux.bodybubby.domain.post.entity.Post;
import com.solux.bodybubby.domain.post.entity.Visibility;
import com.solux.bodybubby.domain.post.repository.PostHashtagRepository;
import com.solux.bodybubby.domain.post.repository.PostRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostHashtagRepository postHashtagRepository;

    /**
     * [마이페이지 메인 조회] GET /api/mypage
     */
    public MyPageResponseDto getMyPageInfo(Long userId) {
        // 1. 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 현재 포인트에 맞는 등급(Tier) 계산
        int currentExp = user.getCurrentExp();
        LevelTier currentTier = LevelTier.getTier(currentExp);

        // 3. DTO 조립 및 반환
        return MyPageResponseDto.builder()
                .userProfile(MyPageResponseDto.UserProfileDto.builder()
                        .nickname(user.getNickname())
                        .profileImageUrl(user.getProfileImageUrl())
                        .introduction(user.getIntroduction())
                        .build())
                .levelInfo(buildLevelInfo(currentExp, currentTier)) // 레벨 계산 로직 분리
                .activitySummary(MyPageResponseDto.ActivitySummaryDto.builder()
                        .completedChallenges(user.getCompletedChallengesCount())
                        .consecutiveAttendance(user.getConsecutiveAttendance())
                        .build())
                .recentBadges(new ArrayList<>()) // 뱃지 기능 연동 전까지 빈 리스트 처리
                .build();
    }

    /**
     * 등급 정보를 바탕으로 상세 레벨 데이터 계산
     */
    private MyPageResponseDto.LevelInfoDto buildLevelInfo(int exp, LevelTier tier) {
        int nextLevelExp = tier.getMaxPoint() + 1; // 다음 등급 시작 점수
        int remainingExp = (tier == LevelTier.MASTER) ? 0 : nextLevelExp - exp; // 마스터 등급은 0 처리

        return MyPageResponseDto.LevelInfoDto.builder()
                .currentLevel(tier.ordinal() + 1) // Enum 순서를 레벨 숫자로 활용 (1~6)
                .levelName(tier.getRankName())    // 등급 명칭
                .currentExp(exp)
                .nextLevelExp((tier == LevelTier.MASTER) ? exp : nextLevelExp)
                .remainingExp(remainingExp)
                .build();
    }

    /**
     * [공개 범위 설정 조회] GET /api/mypage/privacy
     */
    public PrivacySettingsDto getPrivacySettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return PrivacySettingsDto.builder()
                .isWaterPublic(user.isWaterPublic())
                .isWorkoutPublic(user.isWorkoutPublic())
                .isDietPublic(user.isDietPublic())
                .isSleepPublic(user.isSleepPublic())
                .build();
    }

    /**
     * [공개 범위 설정 수정] PATCH /api/mypage/privacy
     */
    @Transactional
    public void updatePrivacySettings(Long userId, PrivacySettingsDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 엔티티의 비즈니스 메서드 호출 (Dirty Checking으로 자동 업데이트)
        user.updatePrivacySettings(
                dto.isWaterPublic(),
                dto.isWorkoutPublic(),
                dto.isDietPublic(),
                dto.isSleepPublic()
        );
    }

    /**
     * [내가 쓴 글 목록 조회] GET /api/mypage/posts
     */
    public List<MyPostDto.Response> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);

        return posts.stream().map(post -> {
            int level = LevelTier.getTier(post.getUser().getCurrentExp()).ordinal() + 1;
            String imageUrl = post.getImages().isEmpty() ? null : post.getImages().get(0).getImageUrl();
            List<String> tags = post.getPostHashtags().stream()
                    .map(ph -> ph.getHashtag().getTagName())
                    .collect(Collectors.toList());

            return MyPostDto.Response.builder()
                    .postId(post.getId())
                    .nickname(post.getUser().getNickname())
                    .userLevel(level)
                    .profileImageUrl(post.getUser().getProfileImageUrl())
                    .createdAt(post.getCreatedAt())
                    .place(post.getTitle())
                    .content(post.getContent())
                    .hashtags(tags)
                    .postImageUrl(imageUrl)
                    .likeCount(post.getLikeCount())
                    .commentCount(post.getComments().size())
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * [내가 쓴 글 수정] PATCH /api/mypage/posts/{postId}
     */
    @Transactional
    public void updateMyPost(Long userId, Long postId, MyPostDto.UpdateRequest request, MultipartFile image) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        // 본인 확인 로직: ErrorCode의 정의된 값 사용
        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UPDATE_PERMISSION_DENIED);
        }

        post.update(request.getTitle(), request.getContent(), Visibility.valueOf(request.getVisibility()));

        // 이미지 삭제 처리
        if (Boolean.TRUE.equals(request.getIsImageDeleted())) {
            post.getImages().clear();
        }
    }

    /**
     * [내가 쓴 글 삭제] DELETE /api/mypage/posts/{postId}
     */
    @Transactional
    public void deleteMyPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.DELETE_PERMISSION_DENIED);
        }

        postRepository.delete(post);
    }
}
