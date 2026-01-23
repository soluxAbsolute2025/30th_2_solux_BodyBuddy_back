package com.solux.bodybubby.domain.shop.service;

import com.solux.bodybubby.domain.shop.dto.BadgeDto;
import com.solux.bodybubby.domain.shop.dto.MyBadgeResponseDto;
import com.solux.bodybubby.domain.shop.dto.ShopDto;
import com.solux.bodybubby.domain.shop.entity.PurchaseHistory;
import com.solux.bodybubby.domain.shop.entity.ShopItem;
import com.solux.bodybubby.domain.shop.repository.PurchaseHistoryRepository;
import com.solux.bodybubby.domain.shop.repository.ShopItemRepository;
import com.solux.bodybubby.domain.user.entity.User;
import com.solux.bodybubby.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShopService {

    private final ShopItemRepository shopItemRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;
    private final UserRepository userRepository;

    public ShopDto.ShopResponse getShopItems(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<ShopItem> items = shopItemRepository.findAllByIsActiveTrue();
        Set<Long> purchasedIds = purchaseHistoryRepository.findAllByUser(user).stream()
                .map(ph -> ph.getItem().getId()).collect(Collectors.toSet());

        List<ShopDto.StandardBadgeDto> standards = items.stream()
                .filter(i -> i.getPrice() == 0) // 무료를 일반으로 분류
                .map(i -> ShopDto.StandardBadgeDto.builder()
                        .id(i.getId()).name(i.getItemName()).unlockCondition(i.getDescription())
                        .unlocked(purchasedIds.contains(i.getId())).iconUrl(i.getImageUrl()).build())
                .collect(Collectors.toList());

        List<ShopDto.PremiumBadgeDto> premiums = items.stream()
                .filter(i -> i.getPrice() > 0) // 유료를 프리미엄으로 분류
                .map(i -> ShopDto.PremiumBadgeDto.builder()
                        .id(i.getId()).name(i.getItemName()).price(i.getPrice())
                        .purchased(purchasedIds.contains(i.getId())).iconUrl(i.getImageUrl()).build())
                .collect(Collectors.toList());

        return ShopDto.ShopResponse.builder().standardBadges(standards).premiumBadges(premiums).build();
    }

    

    @Transactional
    public void purchaseItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow();
        ShopItem item = shopItemRepository.findById(itemId).orElseThrow();

        if (purchaseHistoryRepository.existsByUserAndItem(user, item)) throw new IllegalStateException("이미 보유함");
        if (user.getCurrentPoints() < item.getPrice()) throw new IllegalStateException("포인트 부족");

        user.minusPoints(item.getPrice()); // User 엔티티에 포인트 차감 로직 필요
        purchaseHistoryRepository.save(PurchaseHistory.builder().user(user).item(item).pointsSpent(item.getPrice()).build());
    }

   public MyBadgeResponseDto getMyBadges(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        
        // 1. 내가 구매한 기록만 가져오기
        List<PurchaseHistory> myHistory = purchaseHistoryRepository.findAllByUser(user);

        // 2. DTO 리스트로 변환
        List<BadgeDto> badgeDtos = myHistory.stream()
                .map(h -> BadgeDto.builder()
                        .id(h.getItem().getId())
                        .name(h.getItem().getItemName())
                        .type(h.getItem().getItemType().toString())
                        .iconUrl(h.getItem().getImageUrl())
                        .acquiredAt(h.getPurchasedAt())
                        .build())
                .collect(Collectors.toList());

        // 3. 개수와 목록을 감싸서 반환 (이래야 JSON이 원하시는 대로 나옵니다!)
        return MyBadgeResponseDto.builder()
                .totalBadgeCount(badgeDtos.size())
                .acquiredBadgeCount(badgeDtos.size())
                .badges(badgeDtos)
                .build();
    }

    public ShopDto.RewardStatsResponse getStats(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDateTime.now();

        Integer used = purchaseHistoryRepository.sumPointsSpentByUserInMonth(user, start, end);
        long count = purchaseHistoryRepository.countByUserAndPurchasedAtBetween(user, start, end);

        return ShopDto.RewardStatsResponse.builder()
                .currentPoints(user.getCurrentPoints())
                .earnedPoints(1850) // 포인트 획득은 별도 테이블이 없어 임시값
                .usedPoints(used == null ? 0 : used)
                .rewardCount((int) count).build();
    }
}