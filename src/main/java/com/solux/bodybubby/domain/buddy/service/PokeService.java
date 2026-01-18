package com.solux.bodybubby.domain.buddy.service;

import com.solux.bodybubby.domain.buddy.dto.response.PokedMeResponse;
import com.solux.bodybubby.domain.buddy.entity.Poke;
import com.solux.bodybubby.domain.buddy.repository.BuddyRepository;
import com.solux.bodybubby.domain.buddy.repository.PokeRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import com.solux.bodybubby.global.exception.BusinessException;
import com.solux.bodybubby.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PokeService {
    private final PokeRepository pokeRepository;
    private final UserRepository userRepository;
    private final BuddyRepository buddyRepository;

    @Transactional(readOnly = true)
    public List<PokedMeResponse> getPokedMeList(Long userId) {
        // 1. 기준 시간 설정 (현재로부터 24시간 전)
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        // 2. 최근 나를 찌른 기록 조회
        List<Poke> recentPokes = pokeRepository.findRecentPokes(userId, twentyFourHoursAgo);

        // 3. 중복 제거 및 DTO 변환
        return recentPokes.stream()
                .collect(Collectors.toMap(
                        poke -> poke.getPoker().getId(),
                        poke -> poke,
                        (existing, replacement) -> existing // 최신순 정렬 상태이므로 처음 발견된(가장 최신) 것 유지
                ))
                .values().stream()
                .map(poke -> {
                    // 고도화 포인트: 내가 오늘 이 사람을 이미 맞찔렀는지 확인
                    boolean alreadyPoked = pokeRepository.existsByPokerIdAndPokedIdAndPokedAtAfter(
                            userId,
                            poke.getPoker().getId(),
                            LocalDate.now().atStartOfDay()
                    );

                    return new PokedMeResponse(
                            poke.getPoker().getId(),
                            poke.getPoker().getNickname(),
                            poke.getPoker().getProfileImageUrl(),
                            poke.getPoker().getLevel(),
                            poke.getPokedAt(),
                            alreadyPoked // 추가된 필드 설정
                    );
                })
                .sorted(Comparator.comparing(PokedMeResponse::pokedAt).reversed())
                .collect(Collectors.toList());
    }

    public void pokeUser(Long pokerId, Long pokedId) {
        if (pokerId.equals(pokedId)) {
            throw new BusinessException(ErrorCode.SELF_BUDDY_REQUEST);
        }

        if (!buddyRepository.isBuddy(pokerId, pokedId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        if (pokeRepository.existsByPokerIdAndPokedIdAndPokedAtAfter(pokerId, pokedId, startOfToday)) {
            throw new BusinessException(ErrorCode.ALREADY_POKED_TODAY);
        }

        User poker = userRepository.findById(pokerId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User poked = userRepository.findById(pokedId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Poke poke = Poke.builder()
                .poker(poker)
                .poked(poked)
                .pokedAt(LocalDateTime.now())
                .build();

        pokeRepository.save(poke);
    }
}