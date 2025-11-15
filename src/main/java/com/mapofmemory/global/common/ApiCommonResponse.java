package com.mapofmemory.global.common;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                @ApiResponse(responseCode = "401", description = "인증이 필요합니다.", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                @ApiResponse(responseCode = "403", description = "권한이 없는 리소스에 접근하려고 합니다.", content = @Content(schema = @Schema(implementation = CommonResponse.class))),
                @ApiResponse(responseCode = "404", description = "요청한 리소스를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = CommonResponse.class)))
        }
)
public @interface ApiCommonResponse {
}
