package com.solux.bodybubby.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // --- 공통 에러 (C000) ---
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "올바르지 않은 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다."),

    // --- 유저 관련 에러 (U000) ---
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 사용자입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "U002", "접근 권한이 없습니다."),
    DUPLICATE_LOGIN_ID(HttpStatus.BAD_REQUEST, "U003", "이미 존재하는 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "U004", "이미 사용 중인 이메일입니다."),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "U005", "인증 번호가 일치하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "U006", "이미 사용 중인 닉네임입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U007", "비밀번호가 일치하지 않습니다."),

    // --- 게시글 관련 에러 (P000) ---
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "존재하지 않는 게시글입니다."),
    UPDATE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "P002", "수정 권한이 없습니다."),
    DELETE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "P003", "삭제 권한이 없습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "P101", "이미지 업로드에 실패했습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P201", "존재하지 않는 댓글입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}