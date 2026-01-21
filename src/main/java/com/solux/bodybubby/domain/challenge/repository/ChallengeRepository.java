package com.solux.bodybubby.domain.challenge.repository;

import com.solux.bodybubby.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // 그룹 코드로 챌린지 상세 정보 조회 (가입 시 사용)
    Optional<Challenge> findByGroupCode(String groupCode);

    // 특정 유저가 생성한 챌린지 목록 조회
    List<Challenge> findAllByCreatorId(Long creatorId);
}