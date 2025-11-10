package com.mapofmemory.like.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Like 도메인 테스트")
class LikeTest {

    @Test
    @DisplayName("Like 객체 생성 테스트")
    void Like_객체_생성_테스트() {
        // Given
        Long likeId = 1L;

        // When
        Like like = Like.builder()
                .id(1L)
                .build();

        // Then
        assertNotNull(like);
        assertEquals(likeId, like.getId());
    }
}