package com.mapofmemory.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Member 도메인 테스트")
class MemberTest {

    @Test
    @DisplayName("Member 객체 생성 테스트")
    void Member_객체_생성_테스트() {
        // Given
        Long memberId = 1L;
        String name = "testuser";
        String nickname = "testuser";
        int age = 26;

        // When
        Member member = Member.builder()
                .id(memberId)
                .name(name)
                .nickname(nickname)
                .age(age)
                .build();

        // Then
        assertNotNull(member);
        assertEquals(memberId, member.getId());
        assertEquals(name, member.getName());
        assertEquals(nickname, member.getNickname());
        assertEquals(age, member.getAge());
    }
}