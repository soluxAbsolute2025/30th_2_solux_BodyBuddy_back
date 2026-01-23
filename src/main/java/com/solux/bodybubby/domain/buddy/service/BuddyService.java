package com.solux.bodybubby.domain.buddy.service;

import com.solux.bodybubby.domain.buddy.dto.response.AchievementDto;
import com.solux.bodybubby.domain.buddy.dto.response.BuddyDetailResponse;
import com.solux.bodybubby.domain.buddy.dto.response.BuddyListResponse;
import com.solux.bodybubby.domain.buddy.entity.Buddy;
import com.solux.bodybubby.domain.buddy.entity.BuddyStatus;
import com.solux.bodybubby.domain.buddy.repository.BuddyRepository;
import com.solux.bodybubby.domain.home.dto.response.HomeResponseDTO;
import com.solux.bodybubby.domain.home.service.HomeService;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuddyService {

    private final BuddyRepository buddyRepository;
    private final UserRepository userRepository;
    private final HomeService homeService;

    public BuddyListResponse getBuddyList(Long userId) {
        List<BuddyListResponse.BuddyInfo> myBuddies = fetchMyBuddies(userId);
        List<BuddyListResponse.BuddyRequestInfo> requests = fetchPendingRequests(userId);

        return new BuddyListResponse(myBuddies, requests);
    }

    // 수락된 친구 목록 조회 및 변환
    private List<BuddyListResponse.BuddyInfo> fetchMyBuddies(Long userId) {
        return buddyRepository.findAllAcceptedBuddies(userId).stream()
                .map(buddy -> {
                    var friend = buddy.getSender().getId().equals(userId) ? buddy.getReceiver() : buddy.getSender();
                    return new BuddyListResponse.BuddyInfo(
                            friend.getId(),
                            friend.getNickname(),
                            friend.getLevel(),
                            "10분 전",
                            false // 추후 수정 예정
                    );
                }).collect(Collectors.toList());
    }

    // 대기 중인 요청 목록 조회 및 변환
    private List<BuddyListResponse.BuddyRequestInfo> fetchPendingRequests(Long userId) {
        return buddyRepository.findAllPendingRequests(userId).stream()
                .map(buddy -> new BuddyListResponse.BuddyRequestInfo(
                        buddy.getId(),
                        buddy.getSender().getId(),
                        buddy.getSender().getNickname(),
                        buddy.getSender().getLevel(),
                        "방금 전" // 추후 수정 예정
                )).collect(Collectors.toList());
    }

    // 버디 아이디(loginId)로 검색
    public BuddyDetailResponse searchByLoginId(Long myId, String targetLoginId) {
        User targetUser = userRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return getBuddyDetailResponse(myId, targetUser);
    }

    // 유저 고유 번호(userId)로 상세 조회
    public BuddyDetailResponse getBuddyDetail(Long myId, Long targetId) {
        User targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return getBuddyDetailResponse(myId, targetUser);
    }

    // 공통 변환 로직 수정
    private BuddyDetailResponse getBuddyDetailResponse(Long myId, User targetUser) {
        String status = buddyRepository.findRelation(myId, targetUser.getId())
                .map(buddy -> {
                    if (buddy.getStatus() == BuddyStatus.ACCEPTED) return "FRIEND";
                    if (buddy.getStatus() == BuddyStatus.PENDING) return "PENDING";
                    return "NONE";
                })
                .orElse("NONE");

        // 1. n분 전 활동 (엔티티 수정 시간을 임시 활동 시간으로 활용)
        String lastActivityTime = calculateLastActivity(targetUser.getUpdatedAt());

        // 2. 목표 달성률 계산 (User 엔티티의 Onboarding 정보 활용)
        HomeResponseDTO homeData = homeService.getHomeData(targetUser.getId());

        return new BuddyDetailResponse(
                targetUser.getId(),
                targetUser.getLoginId(),
                targetUser.getNickname(),
                targetUser.getLevel(),
                targetUser.getProfileImageUrl(),
                status,
                lastActivityTime,
                homeData
        );
    }

    // 활동 시간 계산기 (변화 없음)
    private String calculateLastActivity(java.time.LocalDateTime lastTime) {
        if (lastTime == null) return "활동 정보 없음";
        java.time.Duration duration = java.time.Duration.between(lastTime, java.time.LocalDateTime.now());
        long seconds = duration.getSeconds();

        if (seconds < 60) return "방금 전 활동";
        if (seconds < 3600) return (seconds / 60) + "분 전 활동";
        if (seconds < 86400) return (seconds / 3600) + "시간 전 활동";
        return (seconds / 86400) + "일 전 활동";
    }

    // 버디 요청 보내기
    @Transactional
    public void sendBuddyRequest(Long senderId, Long receiverId) {
        // 1. 자기 자신에게 요청하는지 확인
        if (senderId.equals(receiverId))
            throw new BusinessException(ErrorCode.SELF_BUDDY_REQUEST);

        // 2. 이미 관계가 존재하는지 확인 (중복 요청 방지)
        buddyRepository.findRelation(senderId, receiverId).ifPresent(b -> {
            throw new BusinessException(ErrorCode.DUPLICATE_BUDDY_REQUEST);
        });

        // 3. 발신자, 수신자 유저 엔티티 조회
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 4. 새로운 버디 요청 저장
        Buddy newRequest = Buddy.builder()
                .sender(sender)
                .receiver(receiver)
                .status(BuddyStatus.PENDING)
                .build();

        buddyRepository.save(newRequest);
    }

    // 버디 요청 수락
    @Transactional
    public void acceptBuddyRequest(Long userId, Long requestId) {
        Buddy buddy = buddyRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUDDY_REQUEST_NOT_FOUND));

        // 수신자가 현재 로그인한 유저인지 확인
        if (!buddy.getReceiver().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_BUDDY_RECEIVER);
        }

        buddy.updateStatus(BuddyStatus.ACCEPTED);
    }

    // 버디 요청 거절
    @Transactional
    public void rejectBuddyRequest(Long userId, Long requestId) {
        Buddy buddy = buddyRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUDDY_REQUEST_NOT_FOUND));

        if (!buddy.getReceiver().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_BUDDY_RECEIVER);
        }

        // 상태를 REJECTED로 변경하거나, 아예 삭제(Delete) 처리할 수도 있습니다.
        // buddy.updateStatus(BuddyStatus.REJECTED);

        // 만약 거절 시 데이터를 아예 지우고 싶다면:
        buddyRepository.delete(buddy);
    }

    // 버디 삭제 (이미 친구인 관계만 끊기)
    @Transactional
    public void deleteBuddy(Long userId, Long targetId) {
        // 1. 두 사람 사이의 관계 조회
        Buddy buddy = buddyRepository.findRelation(userId, targetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUDDY_REQUEST_NOT_FOUND));

        // 2. 반드시 'ACCEPTED' 상태여야만 삭제 가능 (요청 취소와 분리)
        if (buddy.getStatus() != BuddyStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.INVALID_BUDDY_STATUS); // 에러코드 추가 권장
        }

        // 3. 삭제 권한 확인
        if (!buddy.getSender().getId().equals(userId) && !buddy.getReceiver().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_BUDDY_OWNER);
        }

        buddyRepository.delete(buddy);
    }

}
