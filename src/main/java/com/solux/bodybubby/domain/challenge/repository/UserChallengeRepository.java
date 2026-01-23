package com.solux.bodybubby.domain.challenge.repository;

import com.solux.bodybubby.domain.challenge.entity.UserChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserChallengeRepository extends JpaRepository<UserChallenge, Long> {

    long countByChallengeId(Long challengeId);

    // 1. 유저가 현재 참여 중인 '진행 중'인 챌린지만 조회 (목표 달성 여부 포함)
    List<UserChallenge> findAllByUserIdAndStatus(Long userId, String status);

    // 2. 1순위: 달성률 내림차순, 2순위: 참여 시간 오름차순 (먼저 온 사람이 높은 순위)
    // achievementRate가 같으면 joinedAt(참여시간)이 빠른 순서대로 정렬
    List<UserChallenge> findAllByChallengeIdOrderByAchievementRateDescJoinedAtAsc(Long challengeId);

    // 3. 특정 챌린지의 그룹 전체 평균 달성률 계산
    @Query("SELECT AVG(uc.achievementRate) FROM UserChallenge uc WHERE uc.challenge.id = :challengeId")
    Double getGroupAverageRate(@Param("challengeId") Long challengeId);

    // 4. 특정 유저가 특정 챌린지에 이미 참여 중인지 확인
    Optional<UserChallenge> findByUserIdAndChallengeId(Long userId, Long challengeId);

    // 5. 특정 유저의 모든 참여 정보 조회 (개인 챌린지 필터링용)
    List<UserChallenge> findAllByUserId(Long userId);

    // 챌린지 ID로 찾아서 달성률(AchievementRate) 내림차순으로 정렬해 가져오는 메서드
    List<UserChallenge> findAllByChallengeIdOrderByAchievementRateDesc(Long challengeId);
}