package com.mapofmemory.member.presentation.docs;

import com.mapofmemory.global.common.ApiCommonResponse;
import com.mapofmemory.global.common.CommonResponse;
import com.mapofmemory.member.application.dto.CreateMemberRequest;
import com.mapofmemory.member.application.dto.MemberInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member API", description = "회원 관련 API Document")
public interface MemberApiDocs {

    @Operation(
            summary = "회원 등록",
            description = "새로운 사용자를 생성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 등록 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<Long>> createMember(@RequestBody CreateMemberRequest request);


    @Operation(
            summary = "회원 정보 조회",
            description = "회원 ID로 사용자의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<MemberInfoResponse>> getMember(@PathVariable Long memberId);
}
