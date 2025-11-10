package com.mapofmemory.memory.domain.service;

import com.mapofmemory.memory.application.dto.CreateMemoryRequest;

public interface MemoryService {

    Long createMemory(Long memberId, CreateMemoryRequest request);
}
