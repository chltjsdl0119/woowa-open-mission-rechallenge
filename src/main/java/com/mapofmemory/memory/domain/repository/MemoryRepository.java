package com.mapofmemory.memory.domain.repository;

import com.mapofmemory.member.domain.Member;
import com.mapofmemory.memory.domain.Memory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

    Optional<Memory> findMemoryById(Long id);

    Page<Memory> findAllByMember(Member member, Pageable pageable);
}
