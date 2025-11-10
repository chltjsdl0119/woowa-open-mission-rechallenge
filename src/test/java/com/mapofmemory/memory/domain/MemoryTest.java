package com.mapofmemory.memory.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Memory 도메인 테스트")
class MemoryTest {

    @Test
    @DisplayName("Memory 객체 생성 테스트")
    void Memory_객체_생성_테스트() {
        // Given
        Long memoryId = 1L;
        String title = "Test Memory";
        String content = "This is a test memory content.";

        // When
        Memory memory = Memory.builder()
                .id(memoryId)
                .title(title)
                .content(content)
                .build();

        // Then
        assertNotNull(memory);
        assertEquals(memoryId, memory.getId());
        assertEquals(title, memory.getTitle());
        assertEquals(content, memory.getContent());
    }
}