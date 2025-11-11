package com.mapofmemory.member.application.dto;

import com.mapofmemory.member.domain.Member;

public record MemberInfoResponse(
        Long id,
        String name,
        String nickname,
        int age
) {

    public static MemberInfoResponse from(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getAge()
        );
    }
}
