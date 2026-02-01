package com.community.domain.post.service.strategy;

import com.community.core.exception.ErrorCode;
import com.community.core.exception.custom.BadRequestException;
import com.community.domain.post.dto.request.PostCreateRequest;
import com.community.domain.post.dto.request.PostUpdateRequest;
import com.community.domain.post.entity.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GalleryPostStrategy implements PostStrategy{

    private static final int MIN_IMAGES = 1;
    private static final int MAX_IMAGES = 20;

    @Override
    public void validateCreate(PostCreateRequest request) {
        List<Long> imageIds = request.getImageIds();

        // 이미지 필수 검증
        if (imageIds == null || imageIds.size() < MIN_IMAGES) {
            throw new BadRequestException(ErrorCode.IMAGE_REQUIRED);
        }
        // 최대 개수 검증
        if (imageIds.size() > MAX_IMAGES) {
            throw new BadRequestException(ErrorCode.TOO_MANY_IMAGES, MAX_IMAGES);
        }
    }

    @Override
    public void validateUpdate(PostUpdateRequest request) {
        List<Long> imageIds = request.getImageIds();

        // 이미지 필수 검증
        if (imageIds == null || imageIds.size() < MIN_IMAGES) {
            throw new BadRequestException(ErrorCode.IMAGE_REQUIRED);
        }
        // 최대 개수 검증
        if (imageIds.size() > MAX_IMAGES) {
            throw new BadRequestException(ErrorCode.TOO_MANY_IMAGES, MAX_IMAGES);
        }
    }

    @Override
    public void beforeCreate(Post post, PostCreateRequest request) {
        // 검증은 validateCreate에서 완료됨
        // beforeCreate는 전처리 목적이므로 추가 로직 없음
    }

    @Override
    public void afterCreate(Post post) {
        if (!post.getImages().isEmpty()) {
            String thumbnailUrl = post.getImages().get(0).getUrl();

            Map<String, Object> extraFields = new HashMap<>();
            extraFields.put("thumbnailUrl", thumbnailUrl);

            post.setExtraFields(extraFields);

            log.debug("thumbnailUrl : {}", thumbnailUrl);
        }
    }

    @Override
    public void beforeUpdate(Post post, PostUpdateRequest request) {
        // 검증은 validateUpdate에서 완료됨
        // 썸네일 설정은 afterUpdate에서 이미지 연결 후 처리
    }

    @Override
    public void afterUpdate(Post post) {
        // 수정 후 첫 번째 이미지로 썸네일 갱신
        if (!post.getImages().isEmpty()) {
            String thumbnailUrl = post.getImages().get(0).getUrl();

            Map<String, Object> extraFields = post.getExtraFields() != null
                    ? new HashMap<>(post.getExtraFields())
                    : new HashMap<>();
            extraFields.put("thumbnailUrl", thumbnailUrl);

            post.setExtraFields(extraFields);
        }
        log.info("갤러리 게시글 수정 완료: postId={}", post.getId());
    }
}
