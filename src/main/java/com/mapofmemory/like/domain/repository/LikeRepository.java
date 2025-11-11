package com.mapofmemory.like.domain.repository;

import com.mapofmemory.like.domain.Like;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.memory.domain.Memory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberAndMemory(Member member, Memory memory);
}
