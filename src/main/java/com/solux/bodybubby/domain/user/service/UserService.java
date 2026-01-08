package com.solux.bodybubby.domain.user.service;

import com.solux.bodybubby.domain.user.dto.UserOnboardingRequestDto;
import com.solux.bodybubby.domain.user.dto.UserSignupRequestDto;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * [회원가입]
     *
     * @param dto 회원가입 요청 데이터 (loginId, password, email)
     */
    @Transactional
    public void signup(UserSignupRequestDto dto) {
        // 1. 아이디 중복 여부 재확인
        if (userRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 2. 유저 엔티티 생성 및 비밀번호 암호화 저장
        User user = User.builder()
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword())) // 보안을 위한 해싱 처리
                .email(dto.getEmail())
                .build();

        userRepository.save(user);
    }

    /**
     * [온보딩 정보 등록]
     * 완료 시 isOnboarded 플래그가 true로 변경
     *
     * @param userId 현재 로그인한 유저의 고유 ID
     * @param dto    온보딩 요청 데이터 (닉네임, 나이, 키, 몸무게, 목표 걸음 수 등)
     */
    @Transactional
    public void completeOnboarding(Long userId, UserOnboardingRequestDto dto) {
        // 1. 대상 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 관심사 리스트를 DB 저장을 위해 콤마 구분 문자열로 변환
        String interestsStr = String.join(",", dto.getInterests());

        // 3. 엔티티 내 비즈니스 로직을 호출하여 정보 업데이트 및 온보딩 상태 변경
        user.completeOnboarding(
                dto.getNickname(),
                dto.getAge(),
                dto.getGender(),
                dto.getHeight(),
                dto.getWeight(),
                dto.getDailyStepGoal(),
                dto.getDailyWorkoutGoal(),
                dto.getDailySleepHoursGoal(),
                dto.getDailySleepMinutesGoal(),
                interestsStr,
                dto.getReferrerId()
        );
    }

    /**
     * [아이디 중복 확인]
     *
     * @param loginId 중복 확인 대상 아이디
     * @return 사용 가능 여부 (true: 사용 가능, false: 중복됨)
     */
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    /**
     * [닉네임 중복 확인]
     *
     * @param nickname 중복 확인 대상 닉네임
     * @return 사용 가능 여부 (true: 사용 가능, false: 중복됨)
     */
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    /**
     * [회원 탈퇴]
     */
    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
    }
}