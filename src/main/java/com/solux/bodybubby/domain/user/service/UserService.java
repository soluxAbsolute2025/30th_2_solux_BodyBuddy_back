package com.solux.bodybubby.domain.user.service;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * [닉네임 중복 확인] - 명세서: /api/users/check-id
     */
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /**
     * [소셜 회원가입] - 명세서: /api/users/signup
     */
    @Transactional
    public Long signUp(String email, String provider, String providerId) {
        User user = User.builder()
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .isOnboarded(false)
                .build();
        return userRepository.save(user).getId();
    }

    /**
     * [온보딩 정보 등록] - 명세서: /api/users/onboarding
     */
    @Transactional
    public void registerOnboarding(Long userId, UserOnboardingRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다. id=" + userId));

        user.updateOnboarding(
                requestDto.getNickname(),
                requestDto.getProfileImageUrl(),
                requestDto.getAge(),
                requestDto.getGender(),
                requestDto.getHeight(),
                requestDto.getWeight(),
                requestDto.getDailyStepGoal(),
                requestDto.getDailyWorkoutGoal(),
                requestDto.getDailySleepGoal(),
                requestDto.getInterests(),
                requestDto.getPrivacyScope(),
                requestDto.isNotificationEnabled(),
                requestDto.getReferrerId()
        );
    }

    /**
     * [회원 탈퇴] - 명세서: /api/user/signout
     * 탈퇴 시 사용자와 연관된 MyPage, 활동 기록 등이 정책에 따라 함께 처리됩니다.
     */
    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("탈퇴시키려는 유저가 존재하지 않습니다. id=" + userId));

        // 연관된 MyPage 데이터 등은 JPA Cascade 설정에 의해 자동 삭제되거나,
        // 여기서 명시적으로 삭제 로직을 호출할 수 있습니다.
        userRepository.delete(user);

        log.info("User with ID {} has been successfully withdrawn.", userId);
    }
}