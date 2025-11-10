package com.mapofmemory.member.application;

import com.mapofmemory.global.exception.BusinessException;
import com.mapofmemory.global.exception.GeneralErrorCode;
import com.mapofmemory.member.application.dto.CreateMemberRequest;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
import com.mapofmemory.member.domain.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Long createMember(CreateMemberRequest request) {
        if (memberRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(GeneralErrorCode.DUPLICATE_NICKNAME);
        }

        Member member = Member.builder()
                .name(request.name())
                .nickname(request.nickname())
                .age(request.age())
                .build();

        return memberRepository.save(member).getId();
    }
}
