package com.solux.bodybubby.domain.shop.controller;

import com.solux.bodybubby.domain.shop.dto.ShopDto;
import com.solux.bodybubby.domain.shop.service.ShopService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/shop/items")
    public ResponseEntity<?> getShopItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(Map.of("status", 200, "data", shopService.getShopItems(userDetails.getId())));
    }

    @PostMapping("/shop/purchase")
    public ResponseEntity<?> purchase(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ShopDto.PurchaseRequest request) {
        shopService.purchaseItem(userDetails.getId(), request.getItemId());
        return ResponseEntity.ok(Map.of("status", 200, "message", "구매 완료"));
    }

    @GetMapping("/shop/my-badges")
    public ResponseEntity<?> getMyBadges(@AuthenticationPrincipal CustomUserDetails userDetails) {
    return ResponseEntity.ok(shopService.getMyBadges(userDetails.getId()));
    }

    @GetMapping("/rewards/stats")
    public ResponseEntity<?> getStats(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(Map.of("status", 200, "data", shopService.getStats(userDetails.getId())));
    }
}