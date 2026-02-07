package com.community.domain.reaction.controller;

import com.community.core.common.dto.ApiResponse;
import com.community.core.security.annotation.CurrentUser;
import com.community.domain.reaction.dto.request.ReactionRequest;
import com.community.domain.reaction.dto.response.ReactionResponse;
import com.community.domain.reaction.service.ReactionService;
import com.community.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
@RestController
@RequestMapping("/api/v1/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    /**
     *  게시글 좋아요
     * @param postId  게시글 ID
     * @param request 요청 DTO
     * @param user 사용자
     * @return 성공 응답
     */
    @PostMapping("/posts/{postId}/reactions")
    public ResponseEntity<ApiResponse<ReactionResponse>> reactToPost(
            @PathVariable Long postId,
            @Valid @RequestBody ReactionRequest request,
            @CurrentUser User user) {

        ReactionResponse response = reactionService.reactToPost(user, postId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     *  댓글 좋아요
     * @param commentId 댓글 ID
     * @param request 요청 DTO
     * @param user 사용자
     * @return 성공 응답
     */
    @PostMapping("/comments/{commentId}/reactions")
    public ResponseEntity<ApiResponse<ReactionResponse>> reactToComment(
            @PathVariable Long commentId,
            @Valid @RequestBody ReactionRequest request,
            @CurrentUser User user
    ){
        ReactionResponse response = reactionService.reactToComment(user, commentId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
