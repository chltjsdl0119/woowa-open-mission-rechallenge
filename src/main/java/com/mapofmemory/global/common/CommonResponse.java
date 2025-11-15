package com.mapofmemory.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"success", "code", "message", "data", "timestamp"})
public record CommonResponse<T>(

        boolean success,
        @JsonInclude(JsonInclude.Include.NON_NULL) Integer code,
        @JsonInclude(JsonInclude.Include.NON_NULL) String message,
        @JsonInclude(JsonInclude.Include.NON_NULL) T data,
        LocalDateTime timestamp
) {

    public static <T> CommonResponse<T> onSuccess(T data) {
        return new CommonResponse<>(true, null, null, data, LocalDateTime.now());
    }

    public static <T> CommonResponse<T> onFailure(String message, int code) {
        return new CommonResponse<>(false, code, message, null, LocalDateTime.now());
    }
}
