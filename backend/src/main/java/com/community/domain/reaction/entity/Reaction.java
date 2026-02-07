package com.community.domain.reaction.entity;

import com.community.core.common.entity.BaseEntity;
import com.community.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "reactions",
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_reactions_user_target",
                columnNames = {"user_id", "target_type", "target_id"}
        )
        })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Reaction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    // ========== 생성자 ==========

    private Reaction(User user, TargetType targetType, Long targetId, ReactionType reactionType) {
        this.user = Objects.requireNonNull(user, "사용자는 필수 입니다.");
        this.targetType = Objects.requireNonNull(targetType, "대상 타입은 필수 입니다.");
        this.targetId = Objects.requireNonNull(targetId, "대상 ID는 필수 입니다.");
        this.reactionType = Objects.requireNonNull(reactionType, "반응 타입은 필수 입니다.");
    }

    // ========== 정적 팩토리 메서드 ==========

    public static Reaction create(User user, TargetType targetType, Long targetId, ReactionType reactionType) {
        return new Reaction(user, targetType, targetId, reactionType);
    }

    // ========== 비즈니스 메서드 ==========

    public void changeType(ReactionType newType) {
        if (this.reactionType == newType) {
            throw new IllegalArgumentException("동일한 반응 타입으로 변경 할 수 없습니다");
        }
        this.reactionType = newType;
    }

    public boolean isLike() {
        return reactionType == ReactionType.LIKE;
    }

    public boolean isDislike() {
        return reactionType == ReactionType.DISLIKE;
    }
}
