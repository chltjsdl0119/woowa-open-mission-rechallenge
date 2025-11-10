package com.mapofmemory.global.exception;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvice {

    private final ErrorResponseFactory errorResponseFactory;

    // BusinessException ----------------------------------------------------

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(HttpServletRequest request, BusinessException ex) {
        String traceId = request.getHeader("X-Request-ID");
        ErrorCode code = ex.getErrorCode();
        log.error("[traceId: {}] [BusinessException] code: {} | message: {}", traceId, code.getMessage(), ex.getMessage(), ex);
        return buildResponse(errorResponseFactory.from(request, ex.getErrorCode()));
    }

    // Validation 관련 ----------------------------------------------------

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
    })
    public ResponseEntity<ErrorResponse> handleValidationExceptions(HttpServletRequest request, Exception ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.warn("[traceId: {}] [ValidationException] msg: {}", traceId, ex.getMessage(), ex);

        if (ex instanceof MethodArgumentNotValidException e) {
            return buildResponse(errorResponseFactory.fromValidation(request, e.getBindingResult(), GeneralErrorCode.INVALID_INPUT_VALUE));
        }
        if (ex instanceof BindException e) {
            return buildResponse(errorResponseFactory.fromValidation(request, e.getBindingResult(), GeneralErrorCode.INVALID_INPUT_VALUE));
        }

        return buildResponse(errorResponseFactory.from(request, GeneralErrorCode.INVALID_INPUT_VALUE));
    }

    // JSON 직렬화 / 역직렬화 ----------------------------------------------------

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            JsonParseException.class
    })
    public ResponseEntity<ErrorResponse> handleJsonProcessingExceptions(HttpServletRequest request, Exception ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.error("[traceId: {}] [JsonProcessing] msg: {}", traceId, ex.getMessage(), ex);
        ErrorCode code = ex instanceof HttpMessageNotWritableException
                ? GeneralErrorCode.INTERNAL_SERVER_ERROR
                : GeneralErrorCode.INVALID_INPUT_VALUE;
        return buildResponse(errorResponseFactory.from(request, code));
    }

    // HTTP 프로토콜 관련 ----------------------------------------------------

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class
    })
    public ResponseEntity<ErrorResponse> handleHttpProtocolExceptions(HttpServletRequest request, Exception ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.warn("[traceId: {}] [HttpProtocol] msg: {}", traceId, ex.getMessage(), ex);

        ErrorCode code = switch (ex) {
            case HttpRequestMethodNotSupportedException ignored -> GeneralErrorCode.METHOD_NOT_ALLOWED;
            case HttpMediaTypeNotSupportedException ignored -> GeneralErrorCode.UNSUPPORTED_MEDIA_TYPE;
            case HttpMediaTypeNotAcceptableException ignored -> GeneralErrorCode.NOT_ACCEPTABLE;
            default -> GeneralErrorCode.BAD_REQUEST;
        };

        return buildResponse(errorResponseFactory.from(request, code));
    }

    // Servlet 요청 파라미터/경로 관련 ----------------------------------------------------

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class
    })
    public ResponseEntity<ErrorResponse> handleMissingParameterExceptions(HttpServletRequest request, Exception ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.warn("[traceId: {}] [MissingParameter] msg: {}", traceId, ex.getMessage(), ex);
        return buildResponse(errorResponseFactory.from(request, GeneralErrorCode.MISSING_PARAMETER));
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(HttpServletRequest request, TypeMismatchException ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.warn("[traceId: {}] [TypeMismatch] msg: {}", traceId, ex.getMessage(), ex);
        return buildResponse(errorResponseFactory.from(request, GeneralErrorCode.INVALID_TYPE_VALUE));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFound(HttpServletRequest request, NoHandlerFoundException ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.warn("[traceId: {}] [NotFound] path: {} msg: {}", traceId, ex.getRequestURL(), ex.getMessage());
        return buildResponse(errorResponseFactory.from(request, GeneralErrorCode.NOT_FOUND));
    }

    // DataAccess 및 서버 내부 예외 ----------------------------------------------------

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(HttpServletRequest request, DataAccessException ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.error("[traceId: {}] [DataAccessException] msg: {}", traceId, ex.getMessage(), ex);
        ErrorResponse error = errorResponseFactory.from(request, GeneralErrorCode.INTERNAL_SERVER_ERROR);
        return buildResponse(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.warn("[traceId: {}] [IllegalArgumentException] msg: {}", traceId, ex.getMessage(), ex);
        ErrorResponse error = errorResponseFactory.from(request, GeneralErrorCode.INVALID_INPUT_VALUE);
        return buildResponse(error);
    }

    // 그 외 처리되지 않은 모든 예외 ----------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(HttpServletRequest request, Exception ex) {
        String traceId = request.getHeader("X-Request-ID");
        log.error("[traceId: {}] [UnhandledException] msg: {}", traceId, ex.getMessage(), ex);
        ErrorResponse error = errorResponseFactory.from(request, GeneralErrorCode.INTERNAL_SERVER_ERROR);
        return buildResponse(error);
    }

    // ----------------------------------------------------

    private ResponseEntity<ErrorResponse> buildResponse(ErrorResponse error) {
        return ResponseEntity.status(error.status()).body(error);
    }
}
