package com.community.domain.user.controller;

import com.community.core.common.dto.ApiResponse;
import com.community.core.security.annotation.CurrentUser;
import com.community.domain.user.dto.request.ProfileUpdateRequest;
import com.community.domain.user.dto.request.SignUpRequest;
import com.community.domain.user.dto.response.ProfileResponse;
import com.community.domain.user.dto.response.UserResponse;
import com.community.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 회원가입
     * @param request 회원가입 DTO
     * @return 가입된 사용자 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> signUp(
            @Valid @RequestBody SignUpRequest request) {
        log.info("[API] 회원가입 요청 email={}", request.getEmail());

        Long userId = userService.singUp(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(userId));
    }

    /**
     *  내정보 조회
     * @param userId 현재 로그인한 사용자 ID (임시로 Header)
     * @return 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(@CurrentUser Long userId){
        log.info("[API] 내 정보 조회: userId={}", userId);
        UserResponse response = userService.getMyInfo(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 프로필 수정
     * @param userId 현재 로그인한 ID
     * @param request 프로필 업데이트 DTO
     * @return 수정된 사용자 정보
     */
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@CurrentUser Long userId,@Valid @RequestBody ProfileUpdateRequest request){
        log.info("[API] 프로필 수정 요청: userId={}", userId);
        userService.updateProfile(userId, request);  // ✅ 수정 호출
        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     *  타 사용자 프로필 조회
     * @param userId 조회할 사용자 ID
     * @return 조회 사용자 프로
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getUserProfile(@PathVariable Long userId){
        log.info("[API] 사용자 프로필 조회: userId={}", userId);

        ProfileResponse response = userService.getUserProfile(userId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
