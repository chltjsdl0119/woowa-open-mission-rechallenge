package com.mapofmemory.like.application;

import com.mapofmemory.global.exception.BusinessException;
import com.mapofmemory.global.exception.GeneralErrorCode;
import com.mapofmemory.like.domain.Like;
import com.mapofmemory.like.domain.repository.LikeRepository;
import com.mapofmemory.like.domain.service.LikeService;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
import com.mapofmemory.memory.domain.Memory;
import com.mapofmemory.memory.domain.repository.MemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final MemoryRepository memoryRepository;

    @Transactional
    @Override
    public Long likeMemory(Long memberId, Long memoryId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.MEMBER_NOT_FOUND));

        Memory memory = memoryRepository.findMemoryById(memoryId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.MEMORY_NOT_FOUND));

        Like like = Like.builder()
                .member(member)
                .memory(memory)
                .build();

        return likeRepository.save(like).getId();
    }
}
