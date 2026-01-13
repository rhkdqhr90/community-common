package com.community.domain.user.entity;

import com.community.core.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLRestriction("delete_at IS NULL")
public class User extends BaseAuditableEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(length = 500)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean emailVerified;

    @Column
    private LocalDateTime lastLoginAt;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private LocalDateTime nicknameChangedAt;

    // ----- 정적 팩토리 메서드 -----

    /**
     * 로컬 회원가입 사용자
     */
    public static User createLocalUser(String email, String nickname, String password) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.password = password;
        user.role = Role.USER;
        user.emailVerified = false;
        return user;
    }
    /**
     * OAuth 회원가입용 사용자
     */
    public static User createOAuthUser(String email, String nickname, String profileImage) {
        User user = new User();
        user.email = email;
        user.password = null;  // OAuth는 비밀번호 없음
        user.nickname = nickname;
        user.profileImage = profileImage;
        user.role = Role.USER;
        user.emailVerified = true;  // OAuth는 이메일 인증 완료로 간주
        return user;
    }

    /**
     * 관리자 생성
     */
    public static User createAdmin(String email, String encodedPassword, String nickname) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.role = Role.ADMIN;
        user.emailVerified = true;
        return user;
    }

    // -------비즈니스 메서드-------
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
    public void updateNickname(String nickname){
        if(this.nicknameChangedAt != null && this. nicknameChangedAt.plusDays(30).isAfter(LocalDateTime.now())){
            throw new IllegalStateException("닉네임은 30일에 한 번만 변경 할 수 있습니다.");
        }
        this.nickname = nickname;
        this.nicknameChangedAt = LocalDateTime.now();
    }
    public void updateProfileImage(String profileImage){
        this.profileImage = profileImage;
    }
    public void updatePassword(String encodedPassword){
        this.password = encodedPassword;
    }
    public void verifyEmail(){
        this.emailVerified = true;
    }
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    public boolean isOAuthUser() {
        return this.password == null;
    }
}
