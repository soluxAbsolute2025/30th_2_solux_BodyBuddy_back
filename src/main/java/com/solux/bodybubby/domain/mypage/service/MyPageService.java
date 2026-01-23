package com.solux.bodybubby.domain.mypage.service;

import com.solux.bodybubby.domain.badge.entity.Badge;
import com.solux.bodybubby.domain.badge.repository.BadgeRepository;
import com.solux.bodybubby.domain.badge.repository.UserBadgeRepository;
import com.solux.bodybubby.domain.mypage.dto.*;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;

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

        // 3. 최근 획득한 뱃지 3개 조회
        // Repository의 findTop4를 활용하되 명세서(max 3)에 맞춰 3개만 매핑합니다.
        List<MyPageResponseDto.RecentBadgeDto> recentBadges = userBadgeRepository.findTop4ByUser_IdOrderByAcquiredAtDesc(userId)
                .stream()
                .limit(3)
                .map(ub -> MyPageResponseDto.RecentBadgeDto.builder()
                        .badgeId(ub.getBadge().getId())
                        .badgeName(ub.getBadge().getName())
                        .badgeImageUrl(ub.getBadge().getIconUrl()) // iconUrl 매핑
                        .acquiredDate(ub.getAcquiredAt().toLocalDate().toString()) // 획득 날짜
                        .build())
                .collect(Collectors.toList());

        // 3. DTO 조립 및 반환
        return MyPageResponseDto.builder()
                .userProfile(MyPageResponseDto.UserProfileDto.builder()
                   .userId(user.getId()) 
                        .nickname(user.getNickname())
                        .profileImageUrl(user.getProfileImageUrl())
                        .introduction(user.getIntroduction())
                        .build())
                .levelInfo(buildLevelInfo(currentExp, currentTier)) // 레벨 계산 로직 분리
                .activitySummary(MyPageResponseDto.ActivitySummaryDto.builder()
                        .completedChallenges(user.getCompletedChallengesCount())
                        .consecutiveAttendance(user.getConsecutiveAttendance())
                        .build())
                .recentBadges(recentBadges)
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
                .levelImageUrl(tier.getIconUrl()) // 등급 이미지 URL 매핑
                .currentExp(exp)
                .nextLevelExp((tier == LevelTier.MASTER) ? exp : nextLevelExp)
                .remainingExp(remainingExp)
                .build();
    }

    /**
     * [버디 등급 조회] GET /api/mypage/levels
     */
    public LevelResponseDto getBuddyLevels(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        int currentExp = user.getCurrentExp();
        LevelTier userTier = LevelTier.getTier(currentExp);

        // 1. 기존에 만드신 buildLevelInfo 메서드를 사용하여 내 정보 생성
        MyPageResponseDto.LevelInfoDto myInfo = buildLevelInfo(currentExp, userTier);

        // 2. 전체 등급 리스트 생성 (Enum 전체를 돌면서 조립)
        List<LevelResponseDto.AllLevelInfo> allLevels = Arrays.stream(LevelTier.values())
                .map(tier -> LevelResponseDto.AllLevelInfo.builder()
                        .level(tier.ordinal() + 1)
                        .rankName(tier.getRankName())
                        .levelImageUrl(tier.getIconUrl())
                        .minPoint(tier.getMinPoint())
                        .maxPoint(tier == LevelTier.MASTER ? null : tier.getMaxPoint())
                        .isMyLevel(tier == userTier) // 디자인의 '내 등급' 표시용
                        .build())
                .collect(Collectors.toList());

        // 3. 전체 데이터 묶어서 반환
        return LevelResponseDto.builder()
                .myLevelInfo(LevelResponseDto.MyLevelInfo.builder()
                        .currentLevel(myInfo.getCurrentLevel())
                        .levelName(myInfo.getLevelName())
                        .levelImageUrl(myInfo.getLevelImageUrl())
                        .currentExp(myInfo.getCurrentExp())
                        .nextLevelExp(myInfo.getNextLevelExp())
                        .remainingExp(myInfo.getRemainingExp())
                        .build())
                .allLevels(allLevels)
                .build();
    }

    /**
     * [뱃지 컬렉션 전체 조회] GET /api/mypage/badges
     */
    public BadgeCollectionDto getBadgeCollection(Long userId) {
        // 1. 시스템의 모든 뱃지 조회
        List<Badge> allBadges = badgeRepository.findAll();

        // 2. 해당 유저가 획득한 뱃지 ID 리스트 추출
        List<Long> acquiredBadgeIds = userBadgeRepository.findAllByUser_Id(userId).stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toList());

        // 3. 전체 뱃지를 돌면서 획득 여부 마킹
        List<BadgeCollectionDto.BadgeItemDto> badgeItems = allBadges.stream()
                .map(badge -> BadgeCollectionDto.BadgeItemDto.builder()
                        .badgeId(badge.getId())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .imageUrl(badge.getIconUrl())
                        .isAcquired(acquiredBadgeIds.contains(badge.getId()))
                        .build())
                .collect(Collectors.toList());

        return BadgeCollectionDto.builder()
                .totalBadgeCount(allBadges.size())
                .acquiredBadgeCount(acquiredBadgeIds.size())
                .badges(badgeItems)
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

        // 확실한 반영을 위해 save 호출 추가
        userRepository.save(user);
    }

    /**
     * [내가 쓴 글 목록 조회] GET /api/mypage/posts
     */
    public List<MyPostDto.Response> getMyPosts(Long userId) {
        List<Post> posts = postRepository.findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(userId);

        return posts.stream().map(post -> {
            int level = LevelTier.getTier(post.getUser().getCurrentExp()).ordinal() + 1;
            String imageUrl = post.getImageUrl();
            List<String> tags = post.getPostHashtags().stream()
                    .map(ph -> ph.getHashtag().getTagName())
                    .collect(Collectors.toList());

            return MyPostDto.Response.builder()
                    .postId(post.getId())
                    .nickname(post.getUser().getNickname())
                    .userLevel(level)
                    .profileImageUrl(post.getUser().getProfileImageUrl())
                    .createdAt(post.getCreatedAt())
                    .place(post.getPlace())
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

        // 1. 이미지 삭제 요청이 온 경우 처리
        String finalImageUrl = post.getImageUrl();
        if (Boolean.TRUE.equals(request.getIsImageDeleted())) {
            finalImageUrl = null;
        }

        // 2. 새로운 이미지 파일이 업로드된 경우 (S3 업로드 로직 등이 필요함)
        if (image != null && !image.isEmpty()) {
            // finalImageUrl = s3Service.upload(image); // 예시: 실제 업로드 후 URL 할당
        }

        // 3. 엔티티 update 메서드 호출
        post.update(request.getTitle(), request.getContent(), Visibility.valueOf(request.getVisibility()), finalImageUrl);
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
