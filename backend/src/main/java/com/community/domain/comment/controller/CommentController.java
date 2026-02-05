package com.community.domain.comment.controller;

import com.community.core.common.dto.ApiResponse;
import com.community.core.security.annotation.CurrentUser;
import com.community.domain.comment.dto.request.CommentCreateRequest;
import com.community.domain.comment.dto.request.CommentUpdateRequest;
import com.community.domain.comment.dto.response.CommentResponse;
import com.community.domain.comment.service.CommentService;
import com.community.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     * @param postId 게시물 ID
     * @param request 요청 DTO
     * @param user 사용자 ID
     * @return 댓글 ID
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request,
            @CurrentUser User user) {

        Long commentId = commentService.createComment(postId,request,user);

        return ResponseEntity.ok(ApiResponse.success(commentId));
    }

    /**
     *  게시글 댓글 목록 조회
     * @param postId 게시글 ID
     * @param user 사용자
     * @return 댓글 목록
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentCount(
            @PathVariable Long postId,
            @CurrentUser(required = false) User user){
        List<CommentResponse> comments = commentService.getComments(postId, user);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    /**
     * 댓글 수정
     * @param commentId 댓글 ID
     * @param request 업데이트 DTO
     * @param user 사용자
     * @return 성공 응답
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request,
            @CurrentUser User user) {

        commentService.updateComment(commentId, request, user);

        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 댓글 삭제
     * @param commentId 댓글 ID
     * @param user 사용자
     * @return 성공 응답
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @CurrentUser User user){
        commentService.deleteComment(commentId, user);
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다."));
    }

    /**
     * 댓글 채택 (QnA)
     * @param commentId 댓글 ID
     * @param user 사용자
     * @return 성공응답
     */
    @PostMapping("/comments/{commentId}/select")
    public ResponseEntity<ApiResponse<Void>> selectComment(
            @PathVariable Long commentId,
            @CurrentUser User user){
        commentService.selectComment(commentId, user);
        return ResponseEntity.ok(ApiResponse.success("답변이 채택되었습니다."));
    }

    /**
     *  댓글 채택 취소 (QnA)
     * @param commentId 댓글 ID
     * @param user 사용자
     * @return 성공응답
     */
    @DeleteMapping( "/comments/{commentId}/select")
    public ResponseEntity<ApiResponse<Void>> unselectComment(
            @PathVariable Long commentId,
            @CurrentUser User user){
        commentService.unselectComment(commentId, user);
        return ResponseEntity.ok(ApiResponse.success("채택이 취소되었습니다."));
            }
}
