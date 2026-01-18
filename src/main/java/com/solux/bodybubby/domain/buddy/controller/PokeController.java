package com.solux.bodybubby.domain.buddy.controller;

import com.solux.bodybubby.domain.buddy.dto.response.PokedMeResponse;
import com.solux.bodybubby.domain.buddy.service.PokeService;
import com.solux.bodybubby.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokes")
@RequiredArgsConstructor
public class PokeController {
    private final PokeService pokeService;

    @GetMapping
    public ResponseEntity<List<PokedMeResponse>> getPokedMeList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(pokeService.getPokedMeList(userDetails.getId()));
    }

    @PostMapping("/{pokedId}")
    public ResponseEntity<Void> poke(
            @PathVariable Long pokedId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        pokeService.pokeUser(userDetails.getId(), pokedId);
        return ResponseEntity.ok().build();
    }

}