package com.mapofmemory.memory.application.dto;

public record UpdateMemoryRequest(
        String title,
        String content
) {
}
