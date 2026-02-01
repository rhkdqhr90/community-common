package com.community.core.security.annotation;

import com.community.core.exception.ErrorCode;
import com.community.core.exception.custom.UnauthorizedException;
import com.community.domain.user.entity.User;
import com.community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final UserRepository userRepository;

    /**
     * @CurrentUser 있고 Long 타입인 경우 처리
     * @param parameter UserId
     * @return 있으면 true 없으면 False
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(CurrentUser.class)) {
            return false;
        }

        Class<?> parameterType = parameter.getParameterType();
        return Long.class.equals(parameterType) || User.class.equals(parameterType);
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer, NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory)  {

        // 1. @CurrentUser 어노테이션의 required 속성 확인
        CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
        boolean required = annotation != null && annotation.required();

        // 2. SecurityContext에서 Authentication 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 3. 미인증 처리
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            if (required) {
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
            }
            return null;  // required = false인 경우 null 반환
        }

        // 4. Principal에서 userId 추출
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Long)) {
            if (required) {
                throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
            }
            return null;
        }

        Long userId = (Long) principal;

        // 5. 파라미터 타입에 따라 반환
        Class<?> parameterType = parameter.getParameterType();

        if (Long.class.equals(parameterType)) {
            return userId;
        }

        if (User.class.equals(parameterType)) {
            // User 엔티티 조회
            return userRepository.findById(userId)
                    .orElseThrow(() -> new UnauthorizedException(ErrorCode.UNAUTHORIZED));
        }

        return null;

    }


}
