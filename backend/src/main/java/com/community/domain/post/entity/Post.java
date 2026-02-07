package com.community.domain.post.entity;

import com.community.core.common.entity.BaseAuditableEntity;
import com.community.domain.board.entity.Board;
import com.community.domain.reaction.entity.Reactable;
import com.community.domain.user.entity.User;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Post extends BaseAuditableEntity implements Reactable {
    /**
     * 제목
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 내용 (HTML)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 소속 게시판
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /**
     * 작성자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 이미지 목록
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    /**
     * 태그 목록
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> tags = new ArrayList<>();

    /**
     * 조회수
     */
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    /**
     * 댓글 수 (비정규화)
     */
    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    /**
     * 좋아요 수 (비정규화)
     */
    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    /**
     * 싫어요 수 (비정규화)
     */
    @Column(name = "dislike_count", nullable = false)
    private int dislikeCount = 0;

    /**
     * 공지글 여부
     */
    @Column(name = "is_notice", nullable = false)
    private boolean isNotice = false;

    /**
     * 익명글 여부
     */
    @Column(name = "is_anonymous", nullable = false)
    private boolean isAnonymous = false;

    /**
     * 게시판 타입별 추가 필드 (JSONB)
     *
     * <p>예: MARKET - {"price": 50000, "tradeStatus": "SELLING", "location": "서울"}</p>
     * <p>예: QNA - {"selectedCommentId": 123, "selectedAt": "2025-01-10T12:00:00"}</p>
     */
    @Type(JsonBinaryType.class)
    @Column(name = "extra_fields", columnDefinition = "jsonb")
    private Map<String, Object> extraFields = new HashMap<>();

    /**
     * 삭제 시각 (Soft Delete)
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // ========== 생성자 ==========

    @Builder
    public Post(String title, String content, Board board, User user, boolean isAnonymous) {
        this.title = title;
        this.content = content;
        this.board = board;
        this.user = user;
        this.isAnonymous = isAnonymous;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 게시글 수정
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 작성자 확인
     */
    public boolean isOwnedBy(User user) {
        return this.user.getId().equals(user.getId());
    }

    /**
     * 조회수 증가
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 댓글 수 증가
     */
    public void incrementCommentCount() {
        this.commentCount++;
    }

    /**
     * 댓글 수 감소
     */
    public void decrementCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }

    /**
     * 좋아요 수 증가
     */
    public void incrementLikeCount() {
        this.likeCount++;
    }

    /**
     * 좋아요 수 감소
     */
    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    /**
     * 싫어요 수 증가
     */
    public void incrementDislikeCount() {
        this.dislikeCount++;
    }

    /**
     * 싫어요 수 감소
     */
    public void decrementDislikeCount() {
        this.dislikeCount = Math.max(0, this.dislikeCount - 1);
    }

    /**
     * 공지글 설정
     */
    public void setNotice(boolean isNotice) {
        this.isNotice = isNotice;
    }

    /**
     * 공지글 토글
     */
    public void toggleNotice() {
        this.isNotice = !this.isNotice;
    }

    /**
     * extra_fields 설정
     */
    public void setExtraFields(Map<String, Object> extraFields) {
        this.extraFields = extraFields;
    }

    /**
     * 이미지 추가
     */
    public void addImage(PostImage image) {
        this.images.add(image);
        image.setPost(this);
    }

    /**
     * 이미지 목록 설정
     */
    public void setImages(List<PostImage> images) {
        this.images.clear();
        images.forEach(this::addImage);
    }
    /**
     * 태그 추가
     */
    public void addTag(PostTag postTag) {
        this.tags.add(postTag);
        postTag.setPost(this);
    }

    /**
     * 태그 목록 설정
     */
    public void setTags(List<PostTag> tags) {
        this.tags.clear();
        tags.forEach(this::addTag);
    }

    // ========== QnA 채택 관련 메서드 ==========

    /**
     * 채택된 댓글 ID 조회 (QnA)
     */
    public Long getSelectedCommentId() {
        if (extraFields == null) {
            return null;
        }
        Object value = extraFields.get("selectedCommentId");
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    /**
     * 채택된 댓글 존재 여부 (QnA)
     */
    public boolean hasSelectedComment() {
        return getSelectedCommentId() != null;
    }

    /**
     * 댓글 채택 (QnA)
     */
    public void selectComment(Long commentId) {
        if (hasSelectedComment()) {
            throw new IllegalStateException("이미 채택된 댓글이 있습니다.");
        }
        if (extraFields == null) {
            extraFields = new HashMap<>();
        }
        extraFields.put("selectedCommentId", commentId);
        extraFields.put("selectedAt", LocalDateTime.now().toString());
    }

    /**
     * 댓글 채택 취소 (QnA)
     */
    public void unselectComment() {
        if (extraFields != null) {
            extraFields.remove("selectedCommentId");
            extraFields.remove("selectedAt");
        }
    }

}
