package com.community.domain.comment.service;

import com.community.core.exception.ErrorCode;
import com.community.core.exception.custom.BadRequestException;
import com.community.core.exception.custom.ForbiddenException;
import com.community.core.exception.custom.NotFoundException;
import com.community.domain.board.entity.BoardType;
import com.community.domain.comment.dto.request.CommentCreateRequest;
import com.community.domain.comment.dto.request.CommentUpdateRequest;
import com.community.domain.comment.dto.response.CommentResponse;
import com.community.domain.comment.entity.Comment;
import com.community.domain.comment.repository.CommentRepository;
import com.community.domain.post.entity.Post;
import com.community.domain.post.repository.PostRepository;
import com.community.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 댓글 생성
     * @param postId 게시물 ID
     * @param request 요청 DTO
     * @param user 사용자
     * @return 생성된 댓글 ID
     */

    public Long createComment(Long postId, CommentCreateRequest request, User user) {
        //게시글 조회
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        if (post.isDeleted()){
            throw new BadRequestException(ErrorCode.POST_ALREADY_DELETED);
        }

        //부모 댓글 조회(대댓글인 경우)
        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId()).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

            if(parent.isDeleted()){
                throw new BadRequestException(ErrorCode.COMMENT_ALREADY_DELETED);
            }
            if (!parent.getPost().getId().equals(post.getId())) {
                throw new BadRequestException(ErrorCode.INVALID_PARENT_COMMENT);
            }
        }
        Comment comment = (parent == null)
                ? Comment.create(post, user, request.getContent(), request.isAnonymous())
                : Comment.createReply(post, user, parent, request.getContent(), request.isAnonymous());

        commentRepository.save(comment);
        post.incrementCommentCount();
        log.info("[COMMENT_CREATE] postId={}, commentId={}, userId={}, depth={}",
                post.getId(), comment.getId(), user.getId(), comment.getDepth());

        return comment.getId();
    }

    /**
     *  게시글 댓글 목록 조회
     * @param postId 게시물 ID
     * @param user 사용자 ID
     * @return 댓글 목록
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId, User user){
        List<Comment> rootComments = commentRepository.findCommentsByPostId(postId);
        List<Comment> allReplies = commentRepository.findRepliesByPostId(postId);

        // parentId -> 대댓글 목록 매핑
        Map<Long, List<Comment>> repliesByParentId = allReplies.stream()
                .collect(Collectors.groupingBy(reply -> reply.getParent().getId()));

        return rootComments.stream()
                .map(comment -> {
                    CommentResponse response = CommentResponse.from(comment, user);

                    List<Comment> replies = repliesByParentId.getOrDefault(comment.getId(), List.of());
                    replies.forEach(reply -> {
                        CommentResponse replyResponse = CommentResponse.from(reply, user);
                        response.addReply(replyResponse);
                    });
                    return response;
                })
                .toList();
    }

    /**
     *  댓글 수정
     * @param commentId 댓글 ID
     * @param request 요청 DTO
     * @param user  사용자 ID
     * */
    public void updateComment(Long commentId, CommentUpdateRequest request, User user){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()){
            throw new BadRequestException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        if (!comment.isOwnedBy(user)) {
            throw new BadRequestException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        comment.update(request.getContent());

        log.info("[COMMENT_UPDATE] commentId={}, userId={}", commentId, user.getId());
    }

    /**
     *  댓글 삭제
     * @param commentId 댓글 ID
     * @param user 사용자 ID
     */
    public void deleteComment(Long commentId, User user){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.isDeleted()){
            throw new BadRequestException(ErrorCode.COMMENT_ALREADY_DELETED);
        }

        if (!comment.isOwnedBy(user)) {
            throw new BadRequestException(ErrorCode.NOT_COMMENT_AUTHOR);
        }

        comment.delete();
        Post post = comment.getPost();
        post.decrementCommentCount();

        log.info("[COMMENT_DELETE] commentId={}, userId={}", commentId, user.getId());
    }

    /**
     *  댓글 채택
     * @param commentId 댓글 ID
     * @param user 사용자 ID
     */
    public void selectComment(Long commentId, User user){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        //삭제된 댓글 확인
        if (comment.isDeleted()){
            throw new BadRequestException(ErrorCode.COMMENT_ALREADY_DELETED);
        }
        Post post = comment.getPost();

        if (post.getBoard().getBoardType() != BoardType.QNA) {
            throw new BadRequestException(ErrorCode.ONLY_QNA_CAN_SELECT);
        }
        // 본인은 채택 불가
        if (comment.isOwnedBy(user)) {
            throw new BadRequestException(ErrorCode.CANNOT_SELECT_OWN);
        }

        //게시글 작성자만 채택
        if (!post.isOwnedBy(user)) {
            throw new ForbiddenException(ErrorCode.ONLY_AUTHOR_CAN_SELECT);
        }

        //  이미 채택된 댓글이 있는지 확인
        if (post.hasSelectedComment()) {
            throw new BadRequestException(ErrorCode.ALREADY_SELECTED);
        }

        comment.select();
        post.selectComment(commentId);


        log.info("[COMMENT_SELECT] commentId={}, postId={}, userId={}",
                commentId, post.getId(), user.getId());
    }

    /**
     *  채택 취소
     * @param commentId 댓글 ID
     * @param user 사용자 ID
     */
    public void unselectComment(Long commentId, User user){
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

        Post post = comment.getPost();

        if (post.getBoard().getBoardType() != BoardType.QNA) {
            throw new BadRequestException(ErrorCode.ONLY_QNA_CAN_SELECT);
        }

        // 게시글 작성자만 취소 가능
        if (!post.isOwnedBy(user)) {
            throw new ForbiddenException(ErrorCode.ONLY_AUTHOR_CAN_SELECT);
        }

        // 채택된 댓글인지 확인
        if (!comment.isSelected()) {
            throw new BadRequestException(ErrorCode.COMMENT_NOT_SELECTED);
        }

        comment.unselect();
        post.unselectComment();

        log.info("[COMMENT_UNSELECT] commentId={}, postId={}, userId={}",
                commentId, post.getId(), user.getId());
    }


}
