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
public class MarketPostStrategy implements PostStrategy{

    private static final int MAX_IMAGES = 10;

    @Override
    public void validateCreate(PostCreateRequest request) {
        Map<String, Object> marketFields = request.getMarketFields();

        if (marketFields == null || marketFields.isEmpty()) {
            throw new BadRequestException(ErrorCode.PRICE_REQUIRED);
        }

        //가격 검증
        Object priceObj = marketFields.get("price");
        if (priceObj == null ) {
            throw new BadRequestException(ErrorCode.PRICE_REQUIRED);
        }
        // 가격 타입 및 범위 검증
        Integer price = convertToInteger(priceObj);
        if (price < 0) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
        }

        // 이미지 개수 검증
        if (request.getImageIds() != null && request.getImageIds().size() > MAX_IMAGES) {
            throw new BadRequestException(ErrorCode.TOO_MANY_IMAGES, MAX_IMAGES);
        }

        // 위치 정보 검증 (위도/경도는 쌍으로 있어야 함)
        validateLocation(marketFields);

    }

    @Override
    public void validateUpdate(PostUpdateRequest request) {
        Map<String, Object> marketFields = request.getMarketFields();

        if (marketFields == null || marketFields.isEmpty()) {
            throw new BadRequestException(ErrorCode.PRICE_REQUIRED);
        }

        //가격 검증
        Object priceObj = marketFields.get("price");
        if (priceObj == null ) {
            throw new BadRequestException(ErrorCode.PRICE_REQUIRED);
        }
        // 가격 타입 및 범위 검증
        Integer price = convertToInteger(priceObj);
        if (price < 0) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
        }

        // 이미지 개수 검증
        if (request.getImageIds() != null && request.getImageIds().size() > MAX_IMAGES) {
            throw new BadRequestException(ErrorCode.TOO_MANY_IMAGES, MAX_IMAGES);
        }

        // 위치 정보 검증 (위도/경도는 쌍으로 있어야 함)
        validateLocation(marketFields);
    }

    @Override
    public void beforeCreate(Post post, PostCreateRequest request) {
        Map<String, Object> marketFields = request.getMarketFields();

        Map<String, Object> extraFields = new HashMap<>();

        extraFields.put("price", marketFields.get("price"));

        String tradeStatus = (String) marketFields.getOrDefault("tradeStatus","SELLING");
        extraFields.put("tradeStatus", tradeStatus);

        // 위치 정보 (선택)
        if (marketFields.containsKey("location")) {
            extraFields.put("location", marketFields.get("location"));
        }
        if (marketFields.containsKey("latitude")) {
            extraFields.put("latitude", marketFields.get("latitude"));
        }
        if (marketFields.containsKey("longitude")) {
            extraFields.put("longitude", marketFields.get("longitude"));
        }

        // 카테고리 (선택)
        if (marketFields.containsKey("category")) {
            extraFields.put("category", marketFields.get("category"));
        }

        post.setExtraFields(extraFields);

        log.debug("장터 게시글 생성: price={}, tradeStatus={}",
                extraFields.get("price"), tradeStatus);


    }

    @Override
    public void afterCreate(Post post) {
        log.info("장터 게시글 생성 완료: postId={}, price={}",
                post.getId(), post.getExtraFields().get("price"));
    }

    @Override
    public void beforeUpdate(Post post, PostUpdateRequest request) {
        Map<String, Object> marketFields = request.getMarketFields();

        // 기존 extraFields 유지하면서 업데이트
        Map<String, Object> extraFields = new HashMap<>(post.getExtraFields());

        // 가격 업데이트
        extraFields.put("price", marketFields.get("price"));

        // 거래상태 업데이트
        if (marketFields.containsKey("tradeStatus")) {
            extraFields.put("tradeStatus", marketFields.get("tradeStatus"));
        }

        // 위치 정보 업데이트
        if (marketFields.containsKey("location")) {
            extraFields.put("location", marketFields.get("location"));
        }
        if (marketFields.containsKey("latitude")) {
            extraFields.put("latitude", marketFields.get("latitude"));
        }
        if (marketFields.containsKey("longitude")) {
            extraFields.put("longitude", marketFields.get("longitude"));
        }

        // 카테고리 업데이트
        if (marketFields.containsKey("category")) {
            extraFields.put("category", marketFields.get("category"));
        }

        post.setExtraFields(extraFields);
    }

    @Override
    public void afterUpdate(Post post) {
        log.info("장터 게시글 수정 완료: postId={}", post.getId());
    }

    /**
     * Object를 Integer로 변환
     */
    private Integer convertToInteger(Object obj) {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST, "가격 형식이 올바르지 않습니다.");
            }
        }
        throw new BadRequestException(ErrorCode.BAD_REQUEST, "가격 형식이 올바르지 않습니다.");
    }

    /**
     * 위치 정보 검증 (위도/경도는 쌍으로 있어야 함)
     */
    private void validateLocation(Map<String, Object> marketFields) {
        boolean hasLatitude = marketFields.containsKey("latitude");
        boolean hasLongitude = marketFields.containsKey("longitude");

        if (hasLatitude != hasLongitude) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST,
                    "위도와 경도는 함께 입력해야 합니다.");
        }

        // 위도/경도 범위 검증
        if (hasLatitude) {
            Double latitude = convertToDouble(marketFields.get("latitude"));
            Double longitude = convertToDouble(marketFields.get("longitude"));

            if (latitude < -90 || latitude > 90) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST,
                        "위도는 -90 ~ 90 사이여야 합니다.");
            }

            if (longitude < -180 || longitude > 180) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST,
                        "경도는 -180 ~ 180 사이여야 합니다.");
            }
        }
    }
    /**
     * Object를 Double로 변환
     */
    private Double convertToDouble(Object obj) {
        if (obj instanceof Double) {
            return (Double) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException e) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST, "좌표 형식이 올바르지 않습니다.");
            }
        }
        throw new BadRequestException(ErrorCode.BAD_REQUEST, "좌표 형식이 올바르지 않습니다.");
    }
}
