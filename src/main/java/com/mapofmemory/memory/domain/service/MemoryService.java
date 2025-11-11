package com.mapofmemory.memory.domain.service;

import com.mapofmemory.global.dto.PageResponse;
import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.application.dto.MemoryInfoResponse;
import org.springframework.data.domain.Pageable;

public interface MemoryService {

    Long createMemory(Long memberId, CreateMemoryRequest request);

    MemoryInfoResponse findMemoryById(Long memoryId);

    PageResponse<MemoryInfoResponse> findAllByMemberId(Long memberId, Pageable pageable);
}
