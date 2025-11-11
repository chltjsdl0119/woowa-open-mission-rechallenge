package com.mapofmemory.memory.domain;

import com.mapofmemory.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Memory 도메인 테스트")
class MemoryTest {

    @Test
    @DisplayName("Memory 객체 생성 테스트")
    void Memory_객체_생성_테스트() {
        // given
        Long memoryId = 1L;
        String title = "Test Memory";
        String content = "This is a test memory content.";

        // when
        Memory memory = Memory.builder()
                .id(memoryId)
                .title(title)
                .content(content)
                .build();

        // then
        assertNotNull(memory);
        assertEquals(memoryId, memory.getId());
        assertEquals(title, memory.getTitle());
        assertEquals(content, memory.getContent());
    }

    @Test
    @DisplayName("Memory 객체 수정 테스트")
    void Memory_객체_수정_테스트() {
        // given
        Memory memory = Memory.builder()
                .id(1L)
                .title("Old Title")
                .content("Old Content")
                .build();

        String newTitle = "New Title";
        String newContent = "New Content";

        // when
        memory.update(newTitle, newContent);

        // then
        assertEquals(newTitle, memory.getTitle());
        assertEquals(newContent, memory.getContent());
    }

    @Test
    @DisplayName("Memory 객체 Member 검증 테스트")
    void Memory_객체_Member_검증_테스트() {
        // given
        Long memberId = 1L;
        Member member = Member.builder()
                .id(memberId)
                .name("testuser")
                .nickname("testuser")
                .age(26)
                .build();

        Memory memory = Memory.builder()
                .id(1L)
                .title("Test Memory")
                .content("This is a test memory content.")
                .member(member)
                .build();

        // when & then
        assertEquals(memberId, memory.getMember().getId());
    }
}