package com.community.domain.user.repository;


import com.community.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
    /**
     * 사용자 목록 조회
     * @param keyword (이메일 OR 닉네임)
     * @param pageable 페이지목록
     * @return 사용자 목록
     */
    Page<User> findAllWithKeyword(String keyword, Pageable pageable);
}
