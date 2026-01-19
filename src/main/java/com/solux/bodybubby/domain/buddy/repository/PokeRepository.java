package com.solux.bodybubby.domain.buddy.repository;

import com.solux.bodybubby.domain.buddy.entity.Poke;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PokeRepository extends JpaRepository<Poke, Long> {
    // 특정 유저가 다른 유저를 특정 시간 이후에 찔렀는지 확인
    boolean existsByPokerIdAndPokedIdAndPokedAtAfter(Long pokerId, Long pokedId, LocalDateTime dateTime);

    // 특정 시간(since) 이후에 나를 찌른 기록들을 최신순으로 조회
    @Query("SELECT p FROM Poke p JOIN FETCH p.poker " +
            "WHERE p.poked.id = :userId AND p.pokedAt >= :since " +
            "ORDER BY p.pokedAt DESC")
    List<Poke> findRecentPokes(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    void deleteByPokedAtBefore(LocalDateTime dateTime);
}
