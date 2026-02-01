package com.community.domain.post.entity;

import com.community.domain.tag.entity.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글-태그 연결 엔티티 (N:M)
 */
@Entity
@Table(name = "post_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게시글
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * 태그
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // ========== 생성자 ==========

    public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }

    // ========== 정적 팩토리 메서드 ==========

    /**
     * PostTag 생성
     */
    public static PostTag of(Post post, Tag tag) {
        return new PostTag(post, tag);
    }

    // ========== 연관관계 편의 메서드 ==========

    /**
     * Post 설정 (양방향 관계)
     */
    public void setPost(Post post) {
        this.post = post;
    }
}