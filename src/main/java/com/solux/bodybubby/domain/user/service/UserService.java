package com.solux.bodybubby.domain.user.service;

import com.solux.bodybubby.domain.mypage.entity.LevelTier;
import com.solux.bodybubby.domain.user.dto.UserRequestDto;
import com.solux.bodybubby.domain.user.dto.UserResponseDto;
import com.solux.bodybubby.domain.user.entity.RefreshToken;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.RefreshTokenRepository;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import com.solux.bodybubby.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Map<String, String> verificationStore = new ConcurrentHashMap<>();
    private final org.springframework.data.redis.core.RedisTemplate<String, String> redisTemplate;

    /**
     * [회원가입]
     *
     * @param dto 회원가입 요청 데이터 (UserRequestDto.Signup)
     */
    @Transactional
    public void signup(UserRequestDto.Signup dto) {
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
     * 유빈 님 피드백 반영: 액세스 토큰 반환 및 리프레시 토큰 Redis 저장
     *
     * @param dto 로그인 요청 데이터 (UserRequestDto.Login)
     * @return 발급된 실제 JWT 액세스 토큰
     */
    @Transactional
    public UserRequestDto.LoginResponse login(UserRequestDto.Login dto) {
        // 1. 아이디로 유저 조회
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 비밀번호 일치 여부 확인 (BCrypt 암호 대조)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 3. 액세스 토큰 생성 (JWT)
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getLoginId());

        // 4. 리프레시 토큰 생성 및 Redis 저장
        // 중복 로그인을 방지하거나 기존 토큰을 갱신하기 위해 유저 ID를 키로 저장합니다.
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(user.getId(), refreshTokenValue);
        // RedisRepository를 통해 저장 (설정한 TTL에 따라 자동 삭제됨)
        refreshTokenRepository.save(refreshToken);

        // 5. 결과 조립하여 반환 (유저 ID와 액세스 토큰을 함께 담음)
        return UserRequestDto.LoginResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .build();
    }

    /**
     * [로그아웃]
     */
    @Transactional
    public void logout(String accessToken) {
        // 1. 토큰에서 유저 ID 추출
        Long userId = jwtTokenProvider.getUserId(accessToken);

        // 2. Redis에서 리프레시 토큰 삭제 (더 이상 새로운 액세스 토큰 발급 불가)
        refreshTokenRepository.deleteById(userId);

        // 3. 액세스 토큰 블랙리스트 등록 (남은 시간 동안만 저장하여 현재 토큰 무효화)
        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(
                accessToken,
                "logout",
                java.time.Duration.ofMillis(expiration)
        );
    }

    /**
     * [회원정보 간단 조회]
     * 액세스 토큰으로 식별된 유저의 핵심 UI 정보를 조회합니다.
     */
    public UserResponseDto.SimpleInfo getSimpleInfo(Long userId) {
        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 2. 현재 포인트를 기반으로 티어(LevelTier) 계산
        LevelTier tier = LevelTier.getTier(user.getCurrentExp());

        // 3. 명세서(image_21f77f.png) 구조에 맞춰 반환
        return UserResponseDto.SimpleInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .currentLevel(tier.ordinal() + 1)
                .levelName(tier.getRankName())
                .levelImageUrl(tier.getIconUrl()) // LevelTier에 추가했던 iconUrl 매핑
                .build();
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

        // 회원 탈퇴 시 Redis에 저장된 리프레시 토큰도 함께 삭제해주는 것이 좋습니다.
        refreshTokenRepository.deleteById(userId);
    }

    /**
     * [온보딩 정보 등록]
     */
    @Transactional
    public void completeOnboarding(Long userId, UserRequestDto.Onboarding dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

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
     */
    @Transactional
    public void updateProfile(Long userId, UserRequestDto.ProfileUpdate dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (dto.getEmail() != null && !user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        user.updateProfile(dto.getNickname(), dto.getIntroduction(), dto.getProfileImageUrl(), dto.getEmail());
    }

    /**
     * [비밀번호 변경]
     */
    @Transactional
    public void updatePassword(Long userId, UserRequestDto.PasswordUpdate dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }
}