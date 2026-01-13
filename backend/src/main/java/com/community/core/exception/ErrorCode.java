package com.community.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // ========== Common (C) ==========
    BAD_REQUEST(400, "C001", "잘못된 요청입니다."),
    UNAUTHORIZED(401, "C002", "인증이 필요합니다."),
    FORBIDDEN(403, "C003", "접근 권한이 없습니다."),
    NOT_FOUND(404, "C004", "리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "C005", "허용되지 않은 메서드입니다."),
    CONFLICT(409, "C006", "리소스 충돌이 발생했습니다."),
    INTERNAL_ERROR(500, "C007", "서버 오류가 발생했습니다."),
    INVALID_INPUT(400, "C008", "입력값이 올바르지 않습니다."),

    // ========== Auth (A) ==========
    INVALID_TOKEN(401, "A001", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "A002", "토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(401, "A003", "유효하지 않은 리프레시 토큰입니다."),
    LOGIN_FAILED(401, "A004", "이메일 또는 비밀번호가 올바르지 않습니다."),
    ALREADY_LOGGED_OUT(400, "A005", "이미 로그아웃된 상태입니다."),

    // ========== User (U) ==========
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다. (ID: %s)"),
    USER_NOT_FOUND_BY_EMAIL(404, "U002", "사용자를 찾을 수 없습니다. (Email: %s)"),
    DUPLICATE_EMAIL(409, "U003", "이미 사용 중인 이메일입니다."),
    DUPLICATE_NICKNAME(409, "U004", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(400, "U005", "비밀번호 형식이 올바르지 않습니다."),
    NICKNAME_CHANGE_LIMIT(400, "U006", "닉네임은 30일에 한 번만 변경할 수 있습니다."),
    FORBIDDEN_NICKNAME(400, "U007", "사용할 수 없는 닉네임입니다."),

    // ========== Board (B) ==========
    BOARD_NOT_FOUND(404, "B001", "게시판을 찾을 수 없습니다. (Slug: %s)"),
    BOARD_NOT_ACTIVE(400, "B002", "비활성화된 게시판입니다."),
    WRITE_NOT_ALLOWED(403, "B003", "글쓰기 권한이 없습니다."),

    // ========== Post (P) ==========
    POST_NOT_FOUND(404, "P001", "게시글을 찾을 수 없습니다. (ID: %s)"),
    POST_ALREADY_DELETED(400, "P002", "이미 삭제된 게시글입니다."),
    NOT_POST_AUTHOR(403, "P003", "게시글 작성자만 수정/삭제할 수 있습니다."),
    IMAGE_REQUIRED(400, "P004", "이미지를 첨부해야 합니다."),
    PRICE_REQUIRED(400, "P005", "가격을 입력해야 합니다."),
    TOO_MANY_IMAGES(400, "P006", "이미지는 최대 %d개까지 첨부할 수 있습니다."),
    TOO_MANY_TAGS(400, "P007", "태그는 최대 %d개까지 추가할 수 있습니다."),

    // ========== Comment (CM) ==========
    COMMENT_NOT_FOUND(404, "CM001", "댓글을 찾을 수 없습니다. (ID: %s)"),
    NOT_COMMENT_AUTHOR(403, "CM002", "댓글 작성자만 수정/삭제할 수 있습니다."),
    INVALID_COMMENT_DEPTH(400, "CM003", "대댓글에는 답글을 달 수 없습니다."),
    ALREADY_SELECTED(400, "CM004", "이미 채택된 답변이 있습니다."),
    CANNOT_SELECT_OWN(400, "CM005", "본인의 댓글은 채택할 수 없습니다."),
    ONLY_AUTHOR_CAN_SELECT(403, "CM006", "게시글 작성자만 채택할 수 있습니다."),

    // ========== Reaction (R) ==========
    CANNOT_REACT_OWN(400, "R001", "본인의 글/댓글에는 반응할 수 없습니다."),

    // ========== File (F) ==========
    FILE_NOT_FOUND(404, "F001", "파일을 찾을 수 없습니다."),
    FILE_TOO_LARGE(400, "F002", "파일 크기가 너무 큽니다. (최대: %dMB)"),
    INVALID_FILE_TYPE(400, "F003", "허용되지 않는 파일 형식입니다."),
    FILE_UPLOAD_FAILED(500, "F004", "파일 업로드에 실패했습니다."),

    // ========== Chat (CH) ==========
    CHAT_ROOM_NOT_FOUND(404, "CH001", "채팅방을 찾을 수 없습니다."),
    NOT_CHAT_PARTICIPANT(403, "CH002", "채팅방 참여자가 아닙니다."),
    CANNOT_CHAT_BLOCKED(400, "CH003", "차단된 사용자와는 채팅할 수 없습니다."),

    // ========== Report (RP) ==========
    ALREADY_REPORTED(400, "RP001", "이미 신고한 대상입니다."),
    CANNOT_REPORT_OWN(400, "RP002", "본인은 신고할 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
