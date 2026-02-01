package com.community.domain.user.entity;

import com.community.core.common.entity.BaseAuditableEntity;
import com.community.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@SQLRestriction("deleted_at IS NULL")
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

    /**
     * 로그인 시각 업데이트
     */
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    /**
     * 닉네임 변경 가능 여부 확인
     * @param intervalDays 닉네임 변경 제한 일수
     * @return 변경 가능 여부
     */
    public boolean canChangeNickname(int intervalDays) {
        if (this.nicknameChangedAt == null) {
            return true;
        }
        return this.nicknameChangedAt.plusDays(intervalDays).isBefore(LocalDateTime.now());
    }

    /**
     * 닉네임 변경
     * 주의: 변경 가능 여부는 서비스 레이어에서 canChangeNickname()으로 먼저 확인해야 함
     * @param nickname 새 닉네임
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.nicknameChangedAt = LocalDateTime.now();
    }

    /**
     * 프로필 이미지 변경
     * @param profileImage 프로필 이미지
     */
    public void updateProfileImage(String profileImage){
        this.profileImage = profileImage;
    }

    /**
     * 비밀 번호 업데이트
     * @param encodedPassword 암호화
     */
    public void updatePassword(String encodedPassword){
        this.password = encodedPassword;
    }

    /**
     * 이메일 인증 완료
     */
    public void verifyEmail(){
        this.emailVerified = true;
    }

    /**
     * 탈퇴처리 소프트
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 삭제 여부
     * @return 삭제여부 확인
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 관리자 여부
     * @return ROLE.ADMIN
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    /**
     * OAuth2 사용자 여부
     * @return Password
     */
    public boolean isOAuthUser() {
        return this.password == null;
    }
}
