package com.community.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;


@Configuration
@EnableJpaAuditing
public class JpaConfig {
    /**
     * 현재 로그인 한 사용자 ID 반환
     * createdBy, updatedBy에 의해 자동 입력
     */
    @Bean
    public AuditorAware<Long> auditorProvider(){
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())){
                return Optional.empty();
            }
            return Optional.empty();
        };
    }
}
