package com.solux.bodybubby.domain.quiz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String question;
    private String options; // "1 L, 1.5 - 2 L, 3 L" 처럼 콤마로 구분
    private String answer;  // "1.5 - 2 L"
    private int rewardExp;  // 50

    // 편의 메서드: String으로 저장된 옵션을 다시 리스트로 바꿔줌
    public List<String> getOptionsList() {
        return List.of(this.options.split(","));
    }

}