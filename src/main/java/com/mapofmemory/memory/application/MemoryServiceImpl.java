package com.mapofmemory.memory.application;

import com.mapofmemory.global.common.PageResponse;
import com.mapofmemory.global.exception.BusinessException;
import com.mapofmemory.global.exception.GeneralErrorCode;
import com.mapofmemory.global.util.ConverterUtils;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.application.dto.MemoryInfoResponse;
import com.mapofmemory.memory.application.dto.UpdateMemoryRequest;
import com.mapofmemory.memory.domain.Memory;
import com.mapofmemory.memory.domain.repository.MemoryRepository;
import com.mapofmemory.memory.domain.service.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    private final MemoryRepository memoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Long createMemory(Long memberId, CreateMemoryRequest request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.MEMBER_NOT_FOUND));

        Memory memory = Memory.builder()
                .title(request.title())
                .content(request.content())
                .member(member)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();

        return memoryRepository.save(memory).getId();
    }

    @Transactional(readOnly = true)
    @Override
    public MemoryInfoResponse findMemoryById(Long memoryId) {
        Memory memory = memoryRepository.findMemoryById(memoryId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.MEMORY_NOT_FOUND));

        return ConverterUtils.map(memory, MemoryInfoResponse::from);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponse<MemoryInfoResponse> findAllByMemberId(Long memberId, Pageable pageable) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.MEMBER_NOT_FOUND));

        Page<Memory> memoryPage = memoryRepository.findAllByMember(member, pageable);

        return PageResponse.of(memoryPage, MemoryInfoResponse::from);
    }

    @Transactional
    @Override
    public MemoryInfoResponse updateMemory(Long memoryId, Long MemberId, UpdateMemoryRequest request) {
        Memory memory = memoryRepository.findMemoryById(memoryId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.MEMORY_NOT_FOUND));

        memory.validateMember(MemberId);

        memory.update(request.title(), request.content());

        return ConverterUtils.map(memory, MemoryInfoResponse::from);
    }

    @Transactional
    @Override
    public void deleteMemory(Long memoryId, Long memberId) {
        Memory memory = memoryRepository.findMemoryById(memoryId)
                .orElseThrow(() -> new BusinessException(GeneralErrorCode.MEMORY_NOT_FOUND));

        memory.validateMember(memberId);

        memoryRepository.delete(memory);
    }
}
