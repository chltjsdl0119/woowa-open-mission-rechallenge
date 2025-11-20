package com.mapofmemory.memory.presentation;

import com.mapofmemory.global.common.CommonResponse;
import com.mapofmemory.global.common.PageResponse;
import com.mapofmemory.like.domain.service.LikeService;
import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.application.dto.MemoryInfoResponse;
import com.mapofmemory.memory.application.dto.UpdateMemoryRequest;
import com.mapofmemory.memory.domain.service.MemoryService;
import com.mapofmemory.memory.presentation.docs.MemoryApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
public class MemoryController implements MemoryApiDocs {

    private final MemoryService memoryService;
    private final LikeService likeService;

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<Long>> createMemory(@RequestParam Long memberId, @RequestBody CreateMemoryRequest request) {
        Long memory = memoryService.createMemory(memberId, request);
        return ResponseEntity.ok(CommonResponse.onSuccess(memory));
    }

    @Override
    @GetMapping("/{memoryId}")
    public ResponseEntity<CommonResponse<MemoryInfoResponse>> getMemory(@PathVariable Long memoryId) {
        MemoryInfoResponse response = memoryService.findMemoryById(memoryId);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @GetMapping
    public ResponseEntity<CommonResponse<PageResponse<MemoryInfoResponse>>> getMemoriesByMember(@RequestParam Long memberId, @PageableDefault Pageable pageable) {
        PageResponse<MemoryInfoResponse> response = memoryService.findAllByMemberId(memberId, pageable);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @GetMapping("/map")
    public ResponseEntity<CommonResponse<List<MemoryInfoResponse>>> getMemoriesInMap(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "0.02") double range
    ) {
        List<MemoryInfoResponse> response = memoryService.findMemoriesInMap(lat, lng, range);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @PutMapping("/{memoryId}")
    public ResponseEntity<CommonResponse<MemoryInfoResponse>> updateMemory(@PathVariable Long memoryId, @RequestParam Long memberId, @RequestBody UpdateMemoryRequest request) {
        MemoryInfoResponse response = memoryService.updateMemory(memoryId, memberId, request);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }

    @Override
    @DeleteMapping("/{memoryId}")
    public ResponseEntity<CommonResponse<Void>> deleteMemory(@PathVariable Long memoryId, @RequestParam Long memberId) {
        memoryService.deleteMemory(memoryId, memberId);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }

    @Override
    @PostMapping("/{memoryId}/like")
    public ResponseEntity<CommonResponse<Long>> likeMemory(@PathVariable Long memoryId, @RequestParam Long memberId) {
        Long likeId = likeService.likeMemory(memberId, memoryId);
        return ResponseEntity.ok(CommonResponse.onSuccess(likeId));
    }

    @Override
    @DeleteMapping("/{memoryId}/like")
    public ResponseEntity<CommonResponse<Void>> unlikeMemory(@PathVariable Long memoryId, @RequestParam Long memberId) {
        likeService.unlikeMemory(memberId, memoryId);
        return ResponseEntity.ok(CommonResponse.onSuccess(null));
    }
}
