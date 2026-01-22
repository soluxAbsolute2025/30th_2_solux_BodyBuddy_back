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

    // 2. 실시간 순위 리스트: 달성률(achievementRate) 기준 내림차순 정렬
    List<UserChallenge> findAllByChallengeIdOrderByAchievementRateDesc(Long challengeId);

    // 3. 특정 챌린지의 그룹 전체 평균 달성률 계산
    @Query("SELECT AVG(uc.achievementRate) FROM UserChallenge uc WHERE uc.challenge.id = :challengeId")
    Double getGroupAverageRate(@Param("challengeId") Long challengeId);

    // 4. 특정 유저가 특정 챌린지에 이미 참여 중인지 확인
    Optional<UserChallenge> findByUserIdAndChallengeId(Long userId, Long challengeId);
}