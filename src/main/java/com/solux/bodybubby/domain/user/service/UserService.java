package com.solux.bodybubby.domain.user.service;

import com.solux.bodybubby.domain.mypage.entity.LevelTier; // 경로 확인 필요
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // 회원가입
    @Transactional
    public void signup(UserRequestDto.Signup dto) {
        if (userRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        User user = User.builder()
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .build();
        userRepository.save(user);
    }

    // 로그인 (JWT + Redis)
    @Transactional
    public UserRequestDto.LoginResponse login(UserRequestDto.Login dto) {
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 토큰 발급
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getLoginId());
        
        // Refresh Token 생성 및 Redis 저장
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(user.getId(), refreshTokenValue);
        refreshTokenRepository.save(refreshToken);

        return UserRequestDto.LoginResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .build();
    }

    // 로그아웃
    @Transactional
    public void logout(String accessToken) {
        Long userId = jwtTokenProvider.getUserId(accessToken);
        refreshTokenRepository.deleteById(userId);

        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue().set(accessToken, "logout", java.time.Duration.ofMillis(expiration));
    }

    // 마이페이지 심플 정보 조회 (레벨/티어 포함)
    public UserResponseDto.SimpleInfo getSimpleInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 경험치 기반 티어 계산
        LevelTier tier = LevelTier.getTier(user.getCurrentExp());

        return UserResponseDto.SimpleInfo.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .currentLevel(tier.ordinal() + 1)
                .levelName(tier.getRankName())
                .levelImageUrl(tier.getIconUrl())
                .build();
    }

    // ID 중복체크
    public boolean isLoginIdAvailable(String loginId) {
        return !userRepository.existsByLoginId(loginId);
    }

    // 닉네임 중복체크
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    // 회원 탈퇴
    @Transactional
    public void withdrawUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        userRepository.delete(user);
        refreshTokenRepository.deleteById(userId);
    }

    // 온보딩 정보 등록
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

    // 프로필 정보 수정 (텍스트)
    @Transactional
    public void updateProfile(Long userId, UserRequestDto.ProfileUpdate dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (dto.getEmail() != null && !user.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        user.updateProfile(dto.getNickname(), dto.getIntroduction(), dto.getProfileImageUrl(), dto.getEmail());
    }

    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long userId, UserRequestDto.PasswordUpdate dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.updatePassword(passwordEncoder.encode(dto.getNewPassword()));
    }
}