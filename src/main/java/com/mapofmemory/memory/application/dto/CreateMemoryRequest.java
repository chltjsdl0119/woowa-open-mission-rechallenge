package com.mapofmemory.memory.application.dto;

public record CreateMemoryRequest(
        String title,
        String content,
        double latitude,
        double longitude
) {
}
