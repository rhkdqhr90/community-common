package com.community.domain.user.service;

import com.community.core.config.properties.AppProperties;
import com.community.core.exception.ErrorCode;
import com.community.core.exception.custom.BadRequestException;
import com.community.core.exception.custom.NotFoundException;
import com.community.domain.user.dto.request.ProfileUpdateRequest;
import com.community.domain.user.dto.request.SignUpRequest;
import com.community.domain.user.dto.response.ProfileResponse;
import com.community.domain.user.dto.response.UserResponse;
import com.community.domain.user.entity.User;
import com.community.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    /**
     * 회원 가입
     *
     */
    @Transactional
    public Long singUp(SignUpRequest request){
        log.info("[SIGNUP] 회원 가입 시도 email ={}",request.getEmail());

        //이메일 중복 확인
        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("[SIGNUP] 이메일 중복: email={}", request.getEmail());
            throw new BadRequestException(ErrorCode.DUPLICATE_EMAIL);
        }

        //닉네임 중복 확인
        if(userRepository.existsByNickname(request.getNickname())){
            log.warn("[SIGNUP] 닉네임 중복: nickname={}", request.getNickname());
            throw new BadRequestException(ErrorCode.DUPLICATE_NICKNAME);
        }

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        //사용자 생성
        User user = User.createLocalUser(request.getEmail(),request.getNickname(),encodedPassword);

        //저장
        User savedUser = userRepository.save(user);
        log.info("[SIGNUP] 회원가입 성공: userId={}, email={}",
                savedUser.getId(), savedUser.getEmail());

        return savedUser.getId();
    }

    /**
     * 내 정보 조회     *
     * @param userId 현재 로그인한 사용자 ID
     * @return 사용자 응답 DTO
     * @throws NotFoundException 사용자를 찾을 수 없을 때
     */
    public UserResponse getMyInfo(Long userId) {
        User user = findUserById(userId);
        return UserResponse.from(user);
    }

    /**
     *  프로필 수정
     * @param userId 현재 로그인한 아이디
     * @param request 프로필업데이트 DTO
     */
    public void updateProfile(Long userId, ProfileUpdateRequest request){
        log.info("[PROFILE_UPDATE] 프로필 수정 시도: userId={}", userId);

        User user = findUserById(userId);

        if(request.getNickname() != null && !request.getNickname().equals(user.getNickname())){
            if(userRepository.existsByNickname(request.getNickname())){
                throw new BadRequestException(ErrorCode.DUPLICATE_NICKNAME);
            }
            int intervalDays = appProperties.getUser().getNicknameChangeIntervalDays();
            if (!user.canChangeNickname(intervalDays)) {
                log.warn("[PROFILE_UPDATE] 닉네임 변경 제한: userId={}, lastChanged={}, intervalDays={}",
                        userId, user.getNicknameChangedAt(), intervalDays);
                throw new BadRequestException(ErrorCode.NICKNAME_CHANGE_LIMIT);
            }
            user.updateNickname(request.getNickname());
            log.info("[PROFILE_UPDATE] 닉네임 변경: userId={}, newNickname={}",
                    userId, request.getNickname());
        }
        if(request.getProfileImage() != null){
            user.updateProfileImage(request.getProfileImage());
            log.info("[PROFILE_UPDATE] 프로필 이미지 변경: userId={}", userId);
        }
    }

    /**
     * 프로필 조회(공개 정보)
     * @param userId 사용자 ID
     * @return 프로필
     */
    public ProfileResponse getUserProfile(Long userId){
        User user = findUserById(userId);
        int postCount = 0;
        int commentCount = 0;
        return ProfileResponse.from(user, postCount, commentCount);
    }

    /**
     * 사용자 조회 (내부 헬퍼 메서드)
     *
     * @param userId 사용자 ID
     * @return User 엔티티
     * @throws NotFoundException 사용자를 찾을 수 없을 때
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 사용자를 찾을 수 없음: userId={}", userId);
                    return new NotFoundException(ErrorCode.USER_NOT_FOUND);
                });
    }
}
