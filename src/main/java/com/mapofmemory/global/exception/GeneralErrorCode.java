package com.mapofmemory.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GeneralErrorCode implements ErrorCode {

    // Business 에러 코드
    DUPLICATE_NICKNAME("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT.value()),
    MEMBER_NOT_FOUND("해당 멤버를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    MEMORY_NOT_FOUND("해당 메모리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    LIKE_NOT_FOUND("해당 좋아요를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),

    // 인증/인가 에러 코드
    UNAUTHORIZED_ACCESS("권한이 없는 리소스에 접근하려고 합니다.", HttpStatus.FORBIDDEN.value()),

    // 4xx 에러 코드
    BAD_REQUEST("잘못된 요청입니다.", HttpStatus.BAD_REQUEST.value()),
    INVALID_STATE("잘못된 상태입니다.", HttpStatus.BAD_REQUEST.value()),
    INVALID_INPUT_VALUE("잘못된 입력입니다.", HttpStatus.BAD_REQUEST.value()),
    INVALID_TYPE_VALUE("잘못된 타입의 값입니다.", HttpStatus.BAD_REQUEST.value()),
    MISSING_PARAMETER("필수 요청 파라미터가 누락되었습니다.", HttpStatus.BAD_REQUEST.value()),
    NOT_FOUND("요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()),
    METHOD_NOT_ALLOWED("허용되지 않은 HTTP 메소드입니다.", HttpStatus.METHOD_NOT_ALLOWED.value()),
    NOT_ACCEPTABLE("요청한 응답 미디어 타입을 지원하지 않습니다.", HttpStatus.NOT_ACCEPTABLE.value()),
    UNSUPPORTED_MEDIA_TYPE("지원하지 않는 미디어 타입입니다.", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),

    // 5xx 에러 코드
    INTERNAL_SERVER_ERROR("예상치 못한 서버 오류입니다. 관리자에게 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    NOT_IMPLEMENTED("요청하신 기능은 아직 구현되지 않았습니다.", HttpStatus.NOT_IMPLEMENTED.value());

    private final String message;
    private final int status;
}
