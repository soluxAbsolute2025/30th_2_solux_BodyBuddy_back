package com.solux.bodybubby.domain.shop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shop_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String itemName;

    private String description;

    private Integer price;

    // BADGE / THEME / ITEM
    @Column(length = 20)
    private String itemType;

    // 연결되는 엔티티 id
    private Long itemRefId;

    private String imageUrl;

    @Builder.Default
    private Boolean isActive = true;
}