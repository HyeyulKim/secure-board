package com.portfolio.board.mypage;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 로그인한 본인 정보만 다루는 API. SecurityConfig에서 이 경로는 인증 필수로 막혀있고,
 * JwtAuthenticationFilter가 SecurityContext에 넣어준 인증 정보(Authentication)에서
 * username을 꺼내 "요청자 = 대상자"를 강제한다 (다른 사람의 마이페이지를 건드릴 수 없음).
 */
@RestController
@RequestMapping("/api/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        return ResponseEntity.ok(myPageService.getProfile(authentication.getName()));
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(Authentication authentication,
                                               @RequestBody ProfileUpdateRequest request) {
        myPageService.updateProfile(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                                @RequestBody PasswordChangeRequest request) {
        myPageService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withdraw(Authentication authentication,
                                          @RequestBody WithdrawRequest request) {
        myPageService.withdraw(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException e) {
        return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
    }
}
