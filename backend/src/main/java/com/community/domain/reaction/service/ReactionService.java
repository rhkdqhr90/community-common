package com.community.domain.reaction.service;

import com.community.core.exception.ErrorCode;
import com.community.core.exception.custom.BadRequestException;
import com.community.core.exception.custom.ForbiddenException;
import com.community.core.exception.custom.NotFoundException;
import com.community.domain.comment.repository.CommentRepository;
import com.community.domain.post.repository.PostRepository;
import com.community.domain.reaction.dto.request.ReactionRequest;
import com.community.domain.reaction.dto.response.ReactionResponse;
import com.community.domain.reaction.entity.Reactable;
import com.community.domain.reaction.entity.Reaction;
import com.community.domain.reaction.entity.ReactionType;
import com.community.domain.reaction.entity.TargetType;
import com.community.domain.reaction.repository.ReactionRepository;
import com.community.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public ReactionResponse reactToPost(User user, Long postId, ReactionRequest request) {
        Reactable target = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));
        return react(user, TargetType.POST, target, request.getReactionType());
    }

    @Transactional
    public ReactionResponse reactToComment(User user, Long commentId, ReactionRequest request) {
        Reactable target = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));
        return react(user, TargetType.COMMENT, target, request.getReactionType());
    }

    private ReactionResponse react(User user, TargetType targetType, Reactable target, ReactionType type) {
        validate(target, user);

        Optional<Reaction> existingOpt = reactionRepository.findByUserIdAndTargetTypeAndTargetId(
                user.getId(), targetType, target.getId()
        );

        if (existingOpt.isPresent()) {
            Reaction existing = existingOpt.get();

            if (existing.getReactionType() == type) {
                // 같은 타입 → 취소
                adjustCount(target, type, -1);
                reactionRepository.delete(existing);

                log.info("[REACTION_CANCEL] userId={}, targetType={}, targetId={}, type={}",
                        user.getId(), targetType, target.getId(), type);
                return ReactionResponse.of(target, null);
            } else {
                // 다른 타입 → 변경
                ReactionType oldType = existing.getReactionType();
                adjustCount(target, oldType, -1);
                adjustCount(target, type, 1);
                existing.changeType(type);

                log.info("[REACTION_CHANGE] userId={}, targetType={}, targetId={}, oldType={}, newType={}",
                        user.getId(), targetType, target.getId(), oldType, type);
                return ReactionResponse.of(target, type);
            }
        }


        Reaction reaction = Reaction.create(user, targetType, target.getId(), type);
        reactionRepository.save(reaction);
        adjustCount(target, type, 1);

        log.info("[REACTION_CREATE] userId={}, targetType={}, targetId={}, type={}",
                user.getId(), targetType, target.getId(), type);
        return ReactionResponse.of(target, type);
    }

    private void validate(Reactable target, User user) {
        if (target.isDeleted()) {
            throw new BadRequestException(ErrorCode.TARGET_ALREADY_DELETED);
        }
        if (target.isOwnedBy(user)) {
            throw new ForbiddenException(ErrorCode.CANNOT_REACT_OWN_CONTENT);
        }
    }

    private void adjustCount(Reactable target, ReactionType type, int delta) {
        if (type == ReactionType.LIKE) {
            if (delta > 0) target.incrementLikeCount();
            else target.decrementLikeCount();
        } else {
            if (delta > 0) target.incrementDislikeCount();
            else target.decrementDislikeCount();
        }
    }
}
