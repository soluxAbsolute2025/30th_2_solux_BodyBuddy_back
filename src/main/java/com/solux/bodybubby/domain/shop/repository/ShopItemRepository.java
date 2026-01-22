package com.solux.bodybubby.domain.shop.repository;

import com.solux.bodybubby.domain.shop.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
    // 현재 활성화되어 있는(판매 중인) 아이템만 가져오기
    List<ShopItem> findAllByIsActiveTrue();
}