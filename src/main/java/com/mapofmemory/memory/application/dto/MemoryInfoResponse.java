package com.mapofmemory.memory.application.dto;

import com.mapofmemory.memory.domain.Memory;

public record MemoryInfoResponse(
        Long id,
        String title,
        String content
) {

    public static MemoryInfoResponse from(Memory memory) {
        return new MemoryInfoResponse(
                memory.getId(),
                memory.getTitle(),
                memory.getContent()
        );
    }
}
