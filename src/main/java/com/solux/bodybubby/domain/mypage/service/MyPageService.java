package com.solux.bodybubby.domain.mypage.service;

import com.solux.bodybubby.domain.badge.repository.UserBadgeRepository;
import com.solux.bodybubby.domain.mypage.dto.BadgeDto;
import com.solux.bodybubby.domain.mypage.dto.MyPageMainResponse;
import com.solux.bodybubby.domain.mypage.entity.LevelTier;
import com.solux.bodybubby.domain.mypage.entity.MyPage;
import com.solux.bodybubby.domain.mypage.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final UserBadgeRepository userBadgeRepository;

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
}
