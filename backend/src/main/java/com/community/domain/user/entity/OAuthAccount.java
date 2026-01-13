package com.community.domain.user.entity;

import com.community.core.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_account",
uniqueConstraints = {
        @UniqueConstraint(columnNames = {"provider", "provider_id"})
})
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class OAuthAccount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String provider;

    @Column(nullable = false, name = "provider_id")
    private String providerId;

    @Column
    private String email;

    //------- 정적 팩토리 메소드-------

    public static OAuthAccount of(User user, String provider, String providerId, String email) {
        OAuthAccount account = new OAuthAccount();
        account.user = user;
        account.provider = provider;
        account.providerId = providerId;
        account.email = email;
        return account;
    }
}
