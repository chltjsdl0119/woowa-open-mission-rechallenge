package com.mapofmemory.memory.domain.service;

import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.application.dto.MemoryInfoResponse;

public interface MemoryService {

    Long createMemory(Long memberId, CreateMemoryRequest request);

    MemoryInfoResponse findMemoryById(Long memoryId);
}
