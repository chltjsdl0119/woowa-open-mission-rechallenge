package com.mapofmemory.like.domain.repository;

import com.mapofmemory.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
