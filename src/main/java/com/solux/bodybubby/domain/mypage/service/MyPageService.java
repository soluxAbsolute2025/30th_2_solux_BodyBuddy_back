package com.solux.bodybubby.domain.mypage.service;

import com.solux.bodybubby.domain.badge.entity.Badge;
import com.solux.bodybubby.domain.badge.entity.UserBadge;
import com.solux.bodybubby.domain.badge.repository.BadgeRepository;
import com.solux.bodybubby.domain.badge.repository.UserBadgeRepository;
import com.solux.bodybubby.domain.mypage.dto.*;
import com.solux.bodybubby.domain.mypage.entity.LevelTier;
import com.solux.bodybubby.domain.mypage.entity.MyPage;
import com.solux.bodybubby.domain.mypage.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;

    @Transactional(readOnly = true)
    public MyPageMainResponse getMyPageMain(Long userId) {
        // 1. 마이페이지 및 레벨 정보 조회
        MyPage myPage = myPageRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("마이페이지 정보를 찾을 수 없습니다."));

        LevelTier tier = LevelTier.getTier(myPage.getCumulativePoints());

        // 2. 최근 획득 뱃지 4개 조회 및 DTO 변환
        List<BadgeDto> recentBadges = userBadgeRepository.findTop4ByUser_IdOrderByAcquiredAtDesc(userId)
                .stream()
                .map(ub -> BadgeDto.builder()
                        .badgeName(ub.getBadge().getName())
                        .badgeImageUrl(ub.getBadge().getIconUrl())
                        .acquiredDate(ub.getAcquiredAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                        .build())
                .collect(Collectors.toList());

        // 3. 전체 데이터 구성 (도전 횟수 등은 추후 연동)
        return MyPageMainResponse.builder()
                .nickname(myPage.getUser().getNickname())
                .statusMessage(myPage.getStatusMessage())
                .rankName(tier.getRankName())
                .currentLevel(myPage.getLevel())
                .cumulativePoints(myPage.getCumulativePoints())
                .recentBadges(recentBadges)
                .build();
    }

    @Transactional(readOnly = true)
    public List<BadgeResponse> getUserBadgeCollection(Long userId) {
        // 1. 시스템의 모든 뱃지 종류 조회
        List<Badge> allBadges = badgeRepository.findAll();

        // 2. 해당 유저가 획득한 뱃지 내역 조회
        List<UserBadge> userBadges = userBadgeRepository.findAllByUser_Id(userId);

        // 3. 획득 내역을 Map으로 변환 (비교 효율성을 위해)
        Map<Long, LocalDateTime> userBadgeMap = userBadges.stream()
                .collect(Collectors.toMap(ub -> ub.getBadge().getId(), UserBadge::getAcquiredAt));

        // 4. 전체 뱃지 리스트를 순회하며 DTO 구성
        return allBadges.stream()
                .map(badge -> BadgeResponse.builder()
                        .badgeId(badge.getId())
                        .name(badge.getName())
                        .description(badge.getDescription())
                        .iconUrl(badge.getIconUrl())
                        .isAcquired(userBadgeMap.containsKey(badge.getId())) // 보유 여부 판단
                        .acquiredAt(userBadgeMap.get(badge.getId())) // 없으면 null
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GoalResponse getMyGoals(Long userId) {
        MyPage myPage = myPageRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("정보를 찾을 수 없습니다."));

        return GoalResponse.builder()
                .waterGoal(myPage.getWaterGoal())
                .mealGoal(myPage.getMealGoal())
                .medicineGoal(myPage.getMedicineGoal())
                .build();
    }

    @Transactional
    public void updateMyGoals(Long userId, GoalRequest goalRequest) {
        MyPage myPage = myPageRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("정보를 찾을 수 없습니다."));

        // 엔티티의 비즈니스 메서드를 호출하여 수정
        myPage.updateGoals(
                goalRequest.getWaterGoal(),
                goalRequest.getMealGoal(),
                goalRequest.getMedicineGoal()
        );
    }
}
