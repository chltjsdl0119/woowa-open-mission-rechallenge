package com.mapofmemory.member.application.dto;

public record CreateMemberRequest(
        String name,
        String nickname,
        int age
) {
}
