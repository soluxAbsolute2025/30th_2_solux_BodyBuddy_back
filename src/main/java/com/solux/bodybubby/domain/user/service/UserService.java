package com.solux.bodybubby.domain.user.service;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.dto.UserSignupRequestDto;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 적용
public class UserService {

    private final UserRepository userRepository;

    /**
     * [회원가입]
     * 새로운 유저 엔티티를 생성하고 DB에 저장합니다.
     */
    @Transactional
    public Long signUp(UserSignupRequestDto requestDto) {
        // 중복 가입 체크 로직을 여기에 추가할 수 있습니다.
        User user = User.builder()
                .loginId(requestDto.getLoginId())
                .password(requestDto.getPassword()) // 실제 서비스 시 PasswordEncoder로 암호화 필수
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .build();

        return userRepository.save(user).getId();
    }

    /**
     * [온보딩 정보 등록]
     * 닉네임, 허용 범위, 추천인 아이디 정보를 업데이트합니다.
     */
    @Transactional
    public void registerOnboarding(Long userId, UserOnboardingRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));

        // 엔티티 내부에 정의된 비즈니스 로직을 호출하여 정보를 업데이트합니다.
        user.updateOnboarding(
                requestDto.getNickname(),
                requestDto.getPrivacyScope(),
                requestDto.getReferrerId()
        );

        // TODO: 상세 목표 리스트(goals)를 Goal 엔티티와 매핑하여 저장하는 로직을 추가해야 합니다.
    }

    /**
     * [회원 탈퇴]
     * 유저 ID를 기반으로 해당 유저 정보를 삭제합니다.
     */
    @Transactional
    public void signOut(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다. id=" + userId));

        userRepository.delete(user);
    }
}