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
    UNAUTHORIZED(HttpStatus.FORBIDDEN, "AUTH001", "로그인이 필요한 서비스입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "AUTH002", "유효하지 않은 토큰입니다."),

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
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P201", "존재하지 않는 댓글입니다."),

    // Attendance & Quiz 관련 에러 코드
    QUIZ_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "오늘의 퀴즈가 등록되지 않았습니다."),
    ALREADY_ATTENDED(HttpStatus.BAD_REQUEST, "A002", "오늘은 이미 출석 체크를 완료했습니다."),
    WRONG_QUIZ_ANSWER(HttpStatus.BAD_REQUEST, "A003", "퀴즈 정답이 틀렸습니다. 다시 시도해주세요."),
    INVALID_QUIZ_ACCESS(HttpStatus.FORBIDDEN, "A004", "권한이 없는 퀴즈 접근입니다."),

    // --- 버디(친구) 관련 에러 (B000) ---
    SELF_BUDDY_REQUEST(HttpStatus.BAD_REQUEST, "B001", "자기 자신에게 친구 요청을 보낼 수 없습니다."),
    ALREADY_BUDDY(HttpStatus.CONFLICT, "B002", "이미 버디 관계인 사용자입니다."),
    DUPLICATE_BUDDY_REQUEST(HttpStatus.CONFLICT, "B003", "이미 처리 대기 중인 버디 요청이 있습니다."),
    BUDDY_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "B004", "존재하지 않는 버디 요청입니다."),
    NOT_BUDDY_RECEIVER(HttpStatus.FORBIDDEN, "B004", "본인에게 온 요청만 처리할 수 있습니다."),
    INVALID_BUDDY_STATUS(HttpStatus.BAD_REQUEST, "B006", "현재 요청 중인 관계에서는 이 작업을 수행할 수 없습니다."),
    NOT_BUDDY_OWNER(HttpStatus.FORBIDDEN, "B007", "버디 삭제 권한이 없습니다."),
  
    // --- 콕찌르기 관련 에러 (PK000) ---
    ALREADY_POKED_TODAY(HttpStatus.BAD_REQUEST, "PK001", "오늘 이미 콕찌르기를 완료했습니다."),
    NOT_BUDDY_RELATION(HttpStatus.FORBIDDEN, "PK002", "버디 상태인 유저만 콕찌르기가 가능합니다."),
  
    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "F001", "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "F002", "파일 용량이 제한을 초과했습니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 오류 */
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F003", "이미지 서버 업로드 중 오류가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F004", "이미지 삭제 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}