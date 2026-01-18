package com.solux.bodybubby.domain.buddy;

import com.solux.bodybubby.domain.buddy.repository.PokeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PokeCleanupScheduler {
    private final PokeRepository pokeRepository;

    // 매일 새벽 4시에 일주일 넘은 콕찌르기 기록 삭제
    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanupOldPokes() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        pokeRepository.deleteByPokedAtBefore(oneWeekAgo);
    }
}