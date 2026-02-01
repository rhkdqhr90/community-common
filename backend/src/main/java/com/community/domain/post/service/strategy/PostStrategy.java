package com.community.domain.post.service.strategy;


import com.community.domain.post.dto.request.PostCreateRequest;
import com.community.domain.post.dto.request.PostUpdateRequest;
import com.community.domain.post.entity.Post;

public interface PostStrategy {

    /**
     * 게시글 생성 시 유효성 검증
     * <p>게시판 타입별 필수 필드 및 제약사항 검증</p>
     * @param request 게시글 요청
     */
    void validateCreate(PostCreateRequest request);

    /**
     * 게시글 수정 시 유효성 검증
     *
     * @param request 게시글 수정 요청
     * @throws com.community.core.exception.custom.BadRequestException 검증 실패 시
     */
    void validateUpdate(PostUpdateRequest request);

    /**
     * 게시글 생성 전처리
     * @param post 생성될 게시글 엔티티
     * @param request 생성요청
     */
    void beforeCreate(Post post, PostCreateRequest request);

    /**
     * 게시글 생성 후 처리
     * @param post 생성된 게시글 엔티티
     */
    void afterCreate(Post post);

    /**
     * 게시글 수정 전처리
     * @param post 수정될 게시글 엔티티
     * @param request 수정 요청
     */
    void beforeUpdate(Post post, PostUpdateRequest request);

    /**
     * 게시글 수정 후처리
     * @param post 수정된 게시글 엔티티
     */
    void afterUpdate(Post post);
}
