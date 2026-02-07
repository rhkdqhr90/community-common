package com.community.domain.reaction.repository;

import com.community.domain.reaction.entity.Reaction;
import com.community.domain.reaction.entity.ReactionType;
import com.community.domain.reaction.entity.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    /**
     * 사용자+대상 기준 반응 조회 (단건)
     *
     * @param userId     사용자
     * @param targetType 타입
     * @param targetId   ID
     * @return 반응 (Optional)
     */
    Optional<Reaction> findByUserIdAndTargetTypeAndTargetId(
            Long userId,
            TargetType targetType,
            Long targetId
    );
    /**
     *  대상별 반응 목록 조회
     * @param targetType 타입(Post, Comment)
     * @param targetId 대상 ID
     * @return 목록
     */
    @Query("SELECT r FROM Reaction r JOIN FETCH r.user WHERE r.targetType = :targetType AND r.targetId = :targetId")
    List<Reaction> findAllByTargetTypeAndTargetId(
            @Param("targetType") TargetType targetType,
            @Param("targetId") Long targetId
    );

    /**
     * 대상별 반응 수 카운트 (타입별)
     *
     * @param targetType    타입
     * @param targetId     ID
     * @param reactionType  타입 (LIKE, DISLIKE)
     * @return 반응 수
     */
    Long countByTargetTypeAndTargetIdAndReactionType(
            TargetType targetType,
            Long targetId,
            ReactionType reactionType
    );


    /**
     * 사용자가 누른 반응 목록 조회
     *
     * @param userId 사용자 ID
     * @return 반응 목록
     */
    @Query("SELECT r FROM Reaction r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Reaction> findAllByUserId(@Param("userId") Long userId);

    /**
     * 사용자+대상 목록 일괄 조회
     *
     * @param userId     사용자
     * @param targetType 타입
     * @param targetIds  ID 목록
     * @return 반응 목록
     */
    @Query("SELECT r FROM Reaction r WHERE r.user.id = :userId AND r.targetType = :targetType AND r.targetId IN :targetIds")
    List<Reaction> findAllByUserIdAndTargetTypeAndTargetIdIn(
            @Param("userId") Long userId,
            @Param("targetType") TargetType targetType,
            @Param("targetIds") List<Long> targetIds
    );

    /**
     * 반응 존재 여부 확인
     *
     * @param userId     사용자
     * @param targetType 타입
     * @param targetId   D
     * @return 존재 여부
     */
    boolean existsByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);

    /**
     * 사용자+대상 기준 반응 삭제
     *
     * @param userId     사용자
     * @param targetType 타입
     * @param targetId   ID
     */
    void deleteByUserIdAndTargetTypeAndTargetId(Long userId, TargetType targetType, Long targetId);
}



