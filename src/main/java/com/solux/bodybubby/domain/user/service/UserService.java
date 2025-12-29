package com.solux.bodybubby.domain.user.service;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /** [닉네임 중복 확인] */
    public boolean checkNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /** * [소셜 회원가입/로그인 시 초기 유저 생성]
     * 구글 로그인 성공 후, 이메일과 소셜 정보를 기반으로 최소 정보를 먼저 저장합니다.
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
     * [온보딩 정보 등록]
     * 닉네임, 프로필 이미지, 신체 정보 및 일일 목표를 모두 업데이트합니다.
     */
    @Transactional
    public void registerOnboarding(Long userId, UserOnboardingRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다. id=" + userId));

        // 업데이트된 엔티티의 인자 개수(13개)에 맞춰 모든 정보를 전달합니다.
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

    /** [회원 탈퇴] */
    @Transactional
    public void signOut(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("탈퇴시키려는 유저가 존재하지 않습니다. id=" + userId);
        }
        userRepository.deleteById(userId);
    }
}