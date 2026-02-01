package com.community.core.config;

import com.community.core.config.properties.SecurityPathProperties;
import com.community.core.security.jwt.JwtAuthenticationFilter;
import com.community.core.security.oauth2.CustomOAuth2UserService;
import com.community.core.security.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final SecurityPathProperties securityPathProperties;
    /**
     * 비밀 번호 암호화 인코더
     * @return BCryptPasswordEncoder (강도는 설정에서 관리)
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(securityPathProperties.getPasswordStrength());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     return http
                //Csrf 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                //세션 사용 안함
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 요청 권한 설정 (application.yml에서 관리)
                .authorizeHttpRequests(auth -> {
                        // 공개 API (모든 메서드)
                        securityPathProperties.getPermitAll().forEach(
                                path -> auth.requestMatchers(path).permitAll()
                        );
                        // 공개 API (GET만)
                        securityPathProperties.getPermitGetOnly().forEach(
                                path -> auth.requestMatchers(HttpMethod.GET, path).permitAll()
                        );
                        // 공개 API (POST만)
                        securityPathProperties.getPermitPostOnly().forEach(
                                path -> auth.requestMatchers(HttpMethod.POST, path).permitAll()
                        );
                        // 관리자 API
                        securityPathProperties.getAdminOnly().forEach(
                                path -> auth.requestMatchers(path).hasRole("ADMIN")
                        );
                        // 나머지는 인증 필요
                        auth.anyRequest().authenticated();
                })
                //OAuth2 로그인
                .oauth2Login(oauth2 -> oauth2
                     // OAuth2 로그인 페이지 경로 (프론트엔드로 리다이렉트)
                     .authorizationEndpoint(authEndpoint -> authEndpoint.baseUri("/api/v1/auth/oauth2/authorization"))
                     //OAuth2 콜백 경로
                     .redirectionEndpoint(redEndpoint -> redEndpoint.baseUri("/api/v1/auth/oauth2/redirect"))
                     //사용자 정보 로드
                     .userInfoEndpoint(userinfo -> userinfo.userService(customOAuth2UserService))
                     //성공 핸들러(JWT)
                     .successHandler(oAuth2SuccessHandler))
                //jwt 필터 추가
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
