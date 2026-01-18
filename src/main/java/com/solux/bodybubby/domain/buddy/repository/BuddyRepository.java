package com.solux.bodybubby.domain.buddy.repository;

import com.solux.bodybubby.domain.buddy.entity.Buddy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BuddyRepository extends JpaRepository<Buddy, Long> {
    List<Buddy> findAllBySenderIdOrReceiverId(Long senderId, Long receiverId);

    // 내가 요청했거나, 요청받은 것 중 'ACCEPTED' 상태인 것
    @Query("SELECT b FROM Buddy b WHERE (b.sender.id = :userId OR b.receiver.id = :userId) AND b.status = 'ACCEPTED'")
    List<Buddy> findAllAcceptedBuddies(@Param("userId") Long userId);

    // 나에게 온 요청 목록
    @Query("SELECT b FROM Buddy b WHERE b.receiver.id = :userId AND b.status = 'PENDING'")
    List<Buddy> findAllPendingRequests(@Param("userId") Long userId);

    // 두 사람 사이의 모든 관계(요청자-수신자 상관없이)를 찾는 메소드
    @Query("SELECT b FROM Buddy b WHERE (b.sender.id = :id1 AND b.receiver.id = :id2) " +
            "OR (b.sender.id = :id2 AND b.receiver.id = :id1)")
    Optional<Buddy> findRelation(@Param("id1") Long id1, @Param("id2") Long id2);
}
