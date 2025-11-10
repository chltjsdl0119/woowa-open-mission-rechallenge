package com.mapofmemory.member.domain.service;

import com.mapofmemory.member.application.dto.CreateMemberRequest;

public interface MemberService {

    Long createMember(CreateMemberRequest request);
}
