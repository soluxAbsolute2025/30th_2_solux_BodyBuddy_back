package com.solux.bodybubby.domain.user.service;

import com.solux.bodybubby.domain.user.dto.UserRequestDto;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    //private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, String> verificationStore = new ConcurrentHashMap<>();
    // private final JavaMailSender mailSender;

    /**
     * [회원가입]
     *
     * @param dto 회원가입 요청 데이터 (UserRequestDto.Signup)
     */
    @Transactional
    public void signup(UserRequestDto.Signup dto) { //
        // 1. 아이디 중복 여부 재확인
        if (userRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 2. 유저 엔티티 생성 및 비밀번호 암호화 저장
        User user = User.builder()
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();

        userRepository.save(user);
    }

    /**
     * [로그인]
     *
     * @param dto 로그인 요청 데이터 (UserRequestDto.Login)
     * @return 발급된 인증 토큰 (예: JWT)
     */
    @Transactional
    public String login(UserRequestDto.Login dto) {
        // 1. 아이디로 유저 조회
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 일치 여부 확인
        // passwordEncoder.matches(평문, 암호화된문자열)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD); // 비밀번호 불일치 에러
        }

        // 3. 로그인 성공 시 토큰 생성 및 반환
        //return jwtTokenProvider.createAccessToken(user.getId(), user.getLoginId());
        return "Login Success: " + user.getLoginId();
    }

    /**
     * [로그아웃]
     */
    @Transactional
    public void logout(String accessToken) {
        // TODO: Redis를 사용하여 로그아웃된 토큰(Blacklist)을 저장하고
        // 이후 요청 시 필터에서 이 토큰을 거부하는 로직을 추가해야 함.
        System.out.println("로그아웃 처리된 토큰: " + accessToken);
    }

    /**
     * [아이디 중복 확인]
     */
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    /**
     * [닉네임 중복 확인]
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

    /**
     * [온보딩 정보 등록]
     *
     * @param userId 현재 로그인한 유저의 고유 ID
     * @param dto    온보딩 요청 데이터 (UserRequestDto.Onboarding)
     */
    @Transactional
    public void completeOnboarding(Long userId, UserRequestDto.Onboarding dto) { //
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 관심사 리스트를 DB 저장을 위해 콤마 구분 문자열로 변환
        String interestsStr = dto.getInterests() != null ? String.join(",", dto.getInterests()) : "";

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
     * [프로필 수정]
     *
     * @param userId 현재 로그인한 유저 ID
     * @param dto    프로필 수정 요청 데이터 (UserRequestDto.ProfileUpdate)
     */
    @Transactional
    public void updateProfile(Long userId, UserRequestDto.ProfileUpdate dto) { //
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이메일 변경 시 중복 체크
        if (dto.getEmail() != null && !user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        user.updateProfile(dto.getNickname(), dto.getIntroduction(), dto.getProfileImageUrl(), dto.getEmail());
    }

    /**
     * [비밀번호 변경]
     *
     * @param userId 현재 로그인한 유저 ID
     * @param dto    비밀번호 변경 요청 데이터 (UserRequestDto.PasswordUpdate)
     */
    @Transactional
    public void updatePassword(Long userId, UserRequestDto.PasswordUpdate dto) { //
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }


}