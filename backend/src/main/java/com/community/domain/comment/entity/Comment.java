package com.community.domain.comment.entity;

import com.community.core.common.entity.BaseEntity;
import com.community.domain.post.entity.Post;
import com.community.domain.reaction.entity.Reactable;
import com.community.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
public class Comment extends BaseEntity implements Reactable {

    private static final int MAX_CONTENT_LENGTH = 10000;
    private static final int MAX_DEPTH = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int dislikeCount = 0;

    @Column(nullable = false)
    private int depth = 0;

    @Column(nullable = false)
    private boolean isAnonymous = false;

    @Column(nullable = false)
    private boolean isSelected = false;

    private LocalDateTime deletedAt;

    @Version
    private Long version;

    private Comment(Post post, User user, Comment parent, String content, boolean isAnonymous) {
        this.post = Objects.requireNonNull(post, "게시글은 필수입니다.");
        this.user = Objects.requireNonNull(user, "작성자는 필수입니다.");
        this.parent = parent;
        this.content = validateContent(content);
        this.isAnonymous = isAnonymous;
        this.depth = calculateDepth(parent);
    }

    // ========== 정적 팩토리 메서드 ==========

    /**
     * 댓글 생성 (최상위)
     */
    public static Comment create(Post post, User user, String content, boolean isAnonymous) {
        return new Comment(post, user, null, content, isAnonymous);
    }

    /**
     * 대댓글 생성
     */
    public static Comment createReply(Post post, User user, Comment parent, String content, boolean isAnonymous) {
        Objects.requireNonNull(parent, "대댓글의 부모 댓글은 필수입니다.");
        return new Comment(post, user, parent, content, isAnonymous);
    }

    private static String validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException("댓글은 " + MAX_CONTENT_LENGTH + "자를 초과할 수 없습니다.");
        }
        return content;
    }

    private static int calculateDepth(Comment parent) {
        if (parent == null) {
            return 0;
        }
        int newDepth = parent.getDepth() + 1;
        if (newDepth > MAX_DEPTH) {
            throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다.");
        }
        return newDepth;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 댓글 수정
     */
    public void update(String content) {
        if (isDeleted()) {
            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
        }
        this.content = validateContent(content);
    }

    /**
     * 댓글 삭제 (Soft Delete)
     */
    public void delete() {
        if (isDeleted()) {
            throw new IllegalStateException("이미 삭제된 댓글입니다.");
        }
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 삭제 여부 확인
     */
    public boolean isDeleted() {
        return deletedAt != null;
    }

    /**
     * 작성자 확인
     */
    public boolean isOwnedBy(User user) {
        if (user == null) {
            return false;
        }
        return this.user.getId().equals(user.getId());
    }

    /**
     * 댓글인지 확인 (depth 0)
     */
    public boolean isComment() {
        return depth == 0;
    }

    /**
     * 대댓글인지 확인 (depth 1)
     */
    public boolean isReply() {
        return depth == 1;
    }

    /**
     * 댓글 채택 (QnA)
     */
    public void select() {
        if (isDeleted()) {
            throw new IllegalStateException("삭제된 댓글은 채택할 수 없습니다.");
        }
        if (isSelected) {
            throw new IllegalStateException("이미 채택된 댓글입니다.");
        }
        this.isSelected = true;
    }

    /**
     * 댓글 채택 취소
     */
    public void unselect() {
        if (!isSelected) {
            throw new IllegalStateException("채택되지 않은 댓글입니다.");
        }
        this.isSelected = false;
    }

    /**
     * 좋아요 수 증가
     * Note: 동시성 제어는 @Version을 통한 Optimistic Locking으로 처리
     */
    public void incrementLikeCount() {
        if (isDeleted()) {
            throw new IllegalStateException("삭제된 댓글에는 좋아요를 할 수 없습니다.");
        }
        this.likeCount++;
    }

    /**
     * 좋아요 수 감소
     */
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * 싫어요 수 증가
     */
    public void incrementDislikeCount() {
        if (isDeleted()) {
            throw new IllegalStateException("삭제된 댓글에는 싫어요를 할 수 없습니다.");
        }
        this.dislikeCount++;
    }

    /**
     * 싫어요 수 감소
     */
    public void decrementDislikeCount() {
        if (this.dislikeCount > 0) {
            this.dislikeCount--;
        }
    }
}
