package com.mapofmemory.memory.domain.repository;

import com.mapofmemory.memory.domain.Memory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
}
