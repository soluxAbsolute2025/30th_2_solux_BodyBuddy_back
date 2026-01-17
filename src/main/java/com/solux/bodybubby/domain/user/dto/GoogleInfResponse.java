package com.solux.bodybubby.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data // ⭐ 이게 있어야 getEmail(), getName() 등을 자동으로 만들어줍니다!
@NoArgsConstructor
public class GoogleInfResponse {
    private String email;
    private String email_verified;
    private String at_hash;
    private String name;
    private String picture;
}