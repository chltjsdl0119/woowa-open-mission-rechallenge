package com.mapofmemory.memory.domain.service;

import com.mapofmemory.global.common.PageResponse;
import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.application.dto.MemoryInfoResponse;
import com.mapofmemory.memory.application.dto.UpdateMemoryRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemoryService {

    Long createMemory(Long memberId, CreateMemoryRequest request);

    MemoryInfoResponse findMemoryById(Long memoryId);

    PageResponse<MemoryInfoResponse> findAllByMemberId(Long memberId, Pageable pageable);

    List<MemoryInfoResponse> findMemoriesInMap(double lat, double lng, double range);

    MemoryInfoResponse updateMemory(Long memoryId, Long MemberId, UpdateMemoryRequest request);

    void deleteMemory(Long memoryId, Long memberId);
}
