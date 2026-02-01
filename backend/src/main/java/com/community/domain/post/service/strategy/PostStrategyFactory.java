package com.community.domain.post.service.strategy;

import com.community.domain.board.entity.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 게시판 타입에 따라 적절한 PostStrategy를 반환하는 팩토리
 *
 * <p>Spring Bean으로 등록된 전략들을 주입받아
 * BoardType에 따라 적절한 전략을 매핑합니다.</p>
 *
 * @see PostStrategy
 * @see BoardType
 */
@Component
@RequiredArgsConstructor
public class PostStrategyFactory {
    private final GeneralPostStrategy generalPostStrategy;
    private final GalleryPostStrategy galleryPostStrategy;
    private final MarketPostStrategy marketPostStrategy;
    private final QnaPostStrategy qnaPostStrategy;

    private final Map<BoardType, PostStrategy> strategyMap = new HashMap<>();

    /**
     * Bean 초기화 후 전략 매핑
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        strategyMap.put(BoardType.GENERAL, generalPostStrategy);
        strategyMap.put(BoardType.GALLERY, galleryPostStrategy);
        strategyMap.put(BoardType.MARKET, marketPostStrategy);
        strategyMap.put(BoardType.QNA, qnaPostStrategy);
    }

    /**
     * 게시판 타입에 맞는 전략 반환
     *
     * @param boardType 게시판 타입
     * @return 해당 타입의 전략
     * @throws IllegalArgumentException 알 수 없는 게시판 타입인 경우
     */
    public PostStrategy getStrategy(BoardType boardType) {
        PostStrategy strategy = strategyMap.get(boardType);

        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 게시판 타입입니다: " + boardType);
        }

        return strategy;
    }

    /**
     * QNA 전략 반환 (채택 기능 사용을 위해)
     *
     * @return QnaPostStrategy
     */
    public QnaPostStrategy getQnaStrategy() {
        return qnaPostStrategy;
    }
}
