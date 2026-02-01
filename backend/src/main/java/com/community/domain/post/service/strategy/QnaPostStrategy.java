package com.community.domain.post.service.strategy;

import com.community.core.exception.ErrorCode;
import com.community.core.exception.custom.BadRequestException;
import com.community.domain.post.dto.request.PostCreateRequest;
import com.community.domain.post.dto.request.PostUpdateRequest;
import com.community.domain.post.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class QnaPostStrategy implements PostStrategy{
    private static final int MAX_IMAGES = 5;
    @Override
    public void validateCreate(PostCreateRequest request) {
        if (request.getImageIds() != null && request.getImageIds().size() > MAX_IMAGES) {
            throw new BadRequestException(ErrorCode.TOO_MANY_IMAGES, MAX_IMAGES);
        }
    }

    @Override
    public void validateUpdate(PostUpdateRequest request) {
        if (request.getImageIds() != null && request.getImageIds().size() > MAX_IMAGES) {
            throw new BadRequestException(ErrorCode.TOO_MANY_IMAGES, MAX_IMAGES);
        }
    }

    @Override
    public void beforeCreate(Post post, PostCreateRequest request) {
        // extra_fields 초기화 (채택 정보는 나중에 추가됨)
        Map<String, Object> extraFields = new HashMap<>();
        extraFields.put("selectedCommentId", null);
        extraFields.put("selectedAt", null);

        post.setExtraFields(extraFields);

        log.debug("QnA 게시글 생성: postId={}", post.getId());
    }

    @Override
    public void afterCreate(Post post) {
        log.info("QnA 게시글 생성 완료: postId={}", post.getId());
    }

    @Override
    public void beforeUpdate(Post post, PostUpdateRequest request) {

    }

    @Override
    public void afterUpdate(Post post) {
        log.info("QnA 게시글 수정 완료: postId={}", post.getId());
    }
    /**
     * 답변 채택
     *
     * <p>이 메서드는 PostService에서 별도로 호출됩니다.</p>
     *
     * @param post 게시글
     * @param commentId 채택할 댓글 ID
     * @param selectedAt 채택 시간
     */
    public void selectAnswer(Post post, Long commentId, String selectedAt) {
        Map<String, Object> extraFields = new HashMap<>(post.getExtraFields());

        // 이미 채택된 경우 검증 (PostService에서 선행 검증)
        if (extraFields.get("selectedCommentId") != null) {
            throw new BadRequestException(ErrorCode.ALREADY_SELECTED);
        }

        extraFields.put("selectedCommentId", commentId);
        extraFields.put("selectedAt", selectedAt);

        post.setExtraFields(extraFields);

        log.info("QnA 답변 채택: postId={}, commentId={}", post.getId(), commentId);
    }

    /**
     * 채택 여부 확인
     */
    public boolean isAnswerSelected(Post post) {
        return post.getExtraFields().get("selectedCommentId") != null;
    }

    /**
     * 채택된 댓글 ID 조회
     */
    public Long getSelectedCommentId(Post post) {
        Object selectedCommentId = post.getExtraFields().get("selectedCommentId");
        if (selectedCommentId == null) {
            return null;
        }

        if (selectedCommentId instanceof Long) {
            return (Long) selectedCommentId;
        } else if (selectedCommentId instanceof Integer) {
            return ((Integer) selectedCommentId).longValue();
        } else if (selectedCommentId instanceof String) {
            return Long.parseLong((String) selectedCommentId);
        }

        return null;
    }
}
