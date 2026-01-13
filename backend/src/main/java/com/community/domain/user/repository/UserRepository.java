package com.community.domain.user.repository;

import com.community.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 사용자 조회
    Optional<User> findByEmail(String email);

    //닉네임 사용자 조회
    Optional<User> findByNickname(String nickname);

    //이메일 존재 여부
    boolean existsByEmail(String email);

    //닉네임 존재 여부
    boolean existsByNickname(String nickname);
}
