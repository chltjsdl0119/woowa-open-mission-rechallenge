package com.mapofmemory.memory.presentation;

import com.mapofmemory.global.dto.CommonResponse;
import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.domain.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memories")
@RequiredArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping
    public ResponseEntity<CommonResponse<Long>> createMemory(@RequestParam Long memberId, @RequestBody CreateMemoryRequest request) {
        Long memory = memoryService.createMemory(memberId, request);
        return ResponseEntity.ok(CommonResponse.onSuccess(memory));
    }
}
