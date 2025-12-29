package com.solux.bodybubby.domain.mypage.controller;

import com.solux.bodybubby.domain.mypage.dto.BadgeResponse;
import com.solux.bodybubby.domain.mypage.dto.GoalRequest;
import com.solux.bodybubby.domain.mypage.dto.GoalResponse;
import com.solux.bodybubby.domain.mypage.dto.MyPageMainResponse;
import com.solux.bodybubby.domain.mypage.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage") // 기능 명세서의 기본 URL 반영
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * [마이페이지 메인 조회]
     * 프로필 정보, 레벨, 최근 획득 뱃지 등을 조회합니다.
     */
    @GetMapping // GET /api/mypage
    public ResponseEntity<MyPageMainResponse> getMyPageMain() {
        // TODO: 스프링 시큐리티 적용 후 세션에서 유저 ID를 가져오도록 수정 예정
        // 현재는 구현 및 테스트를 위해 임시로 유저 ID 1L을 사용합니다.
        MyPageMainResponse response = myPageService.getMyPageMain(1L);
        return ResponseEntity.ok(response);
    }

    /**
     * [보유 뱃지 목록 조회]
     * 사용자가 획득한 뱃지와 미획득 뱃지를 포함한 전체 컬렉션을 조회합니다.
     */
    @GetMapping("/badges") // GET /api/mypage/badges
    public ResponseEntity<List<BadgeResponse>> getMyBadges() {
        // 테스트를 위해 유저 ID 1L 사용
        List<BadgeResponse> response = myPageService.getUserBadgeCollection(1L);
        return ResponseEntity.ok(response);
    }

    /**
     * [나의 목표 조회]
     * 사용자가 설정한 수분, 식단, 약 복용 목표 수치를 조회합니다.
     */
    @GetMapping("/goals") // GET /api/mypage/goals
    public ResponseEntity<GoalResponse> getMyGoals() {
        return ResponseEntity.ok(myPageService.getMyGoals(1L));
    }

    /**
     * [나의 목표 수정]
     * 사용자가 입력한 새로운 목표 수치를 저장합니다.
     */
    @PatchMapping("/goals") // PATCH /api/mypage/goals
    public ResponseEntity<Void> updateMyGoals(@RequestBody GoalRequest goalRequest) {
        myPageService.updateMyGoals(1L, goalRequest);
        return ResponseEntity.ok().build();
    }
}