package com.community.domain.tag.entity;

import com.community.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 태그 엔티티
 */
@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag extends BaseEntity {

    /**
     * 태그 이름
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * URL 슬러그
     */
    @Column(nullable = false, unique = true, length = 50)
    private String slug;

    /**
     * 사용 횟수 (비정규화)
     */
    @Column(name = "usage_count", nullable = false)
    private int usageCount = 0;

    // ========== 생성자 ==========

    @Builder
    public Tag(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 태그 생성
     */
    public static Tag create(String name) {
        String slug = createSlug(name);
        return Tag.builder().name(name).slug(slug).build();
    }

    /**
     * 사용 횟수 증가
     */
    public void incrementUsageCount() {
        this.usageCount++;
    }

    /**
     * 사용 횟수 감소
     */
    public void decrementUsageCount() {
        this.usageCount = Math.max(0, this.usageCount - 1);
    }

    /**
     * 슬러그 생성 (한글 → 영문)
     *
     * <p>예: "미술활동" → "미술활동" (한글 그대로)</p>
     * <p>예: "Spring Boot" → "spring-boot"</p>
     */
    public static String createSlug(String name) {
        return name.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9가-힣-]", "");
    }
}