package com.mapofmemory.memory.presentation.docs;

import com.mapofmemory.global.common.ApiCommonResponse;
import com.mapofmemory.global.common.CommonResponse;
import com.mapofmemory.global.common.PageResponse;
import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.application.dto.MemoryInfoResponse;
import com.mapofmemory.memory.application.dto.UpdateMemoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Memory API", description = "기억 관련 API Document")
public interface MemoryApiDocs {

    @Operation(
            summary = "기억 등록",
            description = "새로운 기억을 생성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기억 등록 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<Long>> createMemory(@RequestParam Long memberId, @RequestBody CreateMemoryRequest request);

    @Operation(
            summary = "기억 조회",
            description = "기억 ID로 기억의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기억 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<MemoryInfoResponse>> getMemory(@PathVariable Long memoryId);

    @Operation(
            summary = "회원의 기억 목록 조회",
            description = "회원 ID로 해당 회원이 등록한 기억들의 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원의 기억 목록 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<PageResponse<MemoryInfoResponse>>> getMemoriesByMember(@RequestParam Long memberId, @PageableDefault Pageable pageable);

    @Operation(
            summary = "기억 수정",
            description = "기억 ID로 기억의 정보를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기억 수정 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<MemoryInfoResponse>> updateMemory(@PathVariable Long memoryId, @RequestParam Long memberId, @RequestBody UpdateMemoryRequest request);

    @Operation(
            summary = "기억 삭제",
            description = "기억 ID로 기억을 삭제합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기억 삭제 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<Void>> deleteMemory(@PathVariable Long memoryId, @RequestParam Long memberId);

    @Operation(
            summary = "기억 좋아요",
            description = "기억에 좋아요를 추가합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기억 좋아요 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<Long>> likeMemory(@PathVariable Long memoryId, @RequestParam Long memberId);

    @Operation(
            summary = "기억 좋아요 취소",
            description = "기억에 추가된 좋아요를 취소합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기억 좋아요 취소 성공",
                            content = @Content(schema = @Schema(implementation = CommonResponse.class))
                    )
            }
    )
    @ApiCommonResponse
    ResponseEntity<CommonResponse<Void>> unlikeMemory(@PathVariable Long memoryId, @RequestParam Long memberId);
}
