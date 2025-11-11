package com.mapofmemory.member.domain.service;

import com.mapofmemory.member.application.dto.CreateMemberRequest;
import com.mapofmemory.member.application.dto.MemberInfoResponse;

public interface MemberService {

    Long createMember(CreateMemberRequest request);

    MemberInfoResponse findMemberById(Long memberId);
}
