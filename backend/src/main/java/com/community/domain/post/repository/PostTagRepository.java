package com.community.domain.post.repository;

import com.community.domain.post.entity.PostTag;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Modifying
    @Query("Delete from PostTag pt where pt.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);
}
