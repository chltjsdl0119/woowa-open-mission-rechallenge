package com.mapofmemory.memory.domain.repository;

import com.mapofmemory.member.domain.Member;
import com.mapofmemory.memory.domain.Memory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {

    Optional<Memory> findMemoryById(Long id);

    Page<Memory> findAllByMember(Member member, Pageable pageable);

    @Query(value = """
            SELECT * FROM memories
            FORCE INDEX (idx_memory_location)
            WHERE latitude BETWEEN :minLat AND :maxLat
              AND longitude BETWEEN :minLng AND :maxLng
            """, nativeQuery = true
    )
    List<Memory> findAllInMapWithIndexHint(
            @Param("minLat") double minLat, @Param("maxLat") double maxLat,
            @Param("minLng") double minLng, @Param("maxLng") double maxLng
    );
}
