package com.mapofmemory.like.domain.service;

public interface LikeService {

    Long likeMemory(Long memberId, Long memoryId);

    void unlikeMemory(Long memberId, Long memoryId);
}
