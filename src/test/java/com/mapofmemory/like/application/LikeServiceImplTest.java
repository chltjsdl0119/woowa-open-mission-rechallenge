package com.mapofmemory.like.application;

import com.mapofmemory.global.exception.BusinessException;
import com.mapofmemory.global.exception.GeneralErrorCode;
import com.mapofmemory.like.domain.Like;
import com.mapofmemory.like.domain.repository.LikeRepository;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
import com.mapofmemory.memory.domain.Memory;
import com.mapofmemory.memory.domain.repository.MemoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeServiceImpl 단위 테스트")
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemoryRepository memoryRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Nested
    @DisplayName("likeMemory 메서드는")
    class LikeMemory {

        @Test
        @DisplayName("회원과 메모리가 존재하면 좋아요를 저장한다")
        void 좋아요를_저장한다() {
            // given
            Member member = Member.builder().id(1L).nickname("닉").build();
            Memory memory = Memory.builder().id(10L).title("제목").build();
            Like savedLike = Like.builder().id(100L).member(member).memory(memory).build();

            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));
            given(memoryRepository.findMemoryById(10L)).willReturn(Optional.of(memory));
            given(likeRepository.save(any(Like.class))).willReturn(savedLike);

            // when
            Long likeId = likeService.likeMemory(1L, 10L);

            // then
            assertThat(likeId).isEqualTo(100L);
            verify(memberRepository).findMemberById(1L);
            verify(memoryRepository).findMemoryById(10L);
            verify(likeRepository).save(any(Like.class));
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 예외가 발생한다")
        void 회원이_없으면_예외발생() {
            given(memberRepository.findMemberById(any())).willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.likeMemory(1L, 10L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("메모리가 존재하지 않으면 예외가 발생한다")
        void 메모리가_없으면_예외발생() {
            Member member = Member.builder().id(1L).build();
            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));
            given(memoryRepository.findMemoryById(any())).willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.likeMemory(1L, 10L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("unlikeMemory 메서드는")
    class UnlikeMemory {

        @Test
        @DisplayName("회원과 메모리가 존재하고 좋아요가 존재하면 삭제한다")
        void 좋아요를_삭제한다() {
            // given
            Member member = Member.builder().id(1L).nickname("닉").build();
            Memory memory = Memory.builder().id(10L).title("제목").build();
            Like like = Like.builder().id(100L).member(member).memory(memory).build();

            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));
            given(memoryRepository.findMemoryById(10L)).willReturn(Optional.of(memory));
            given(likeRepository.findByMemberAndMemory(member, memory)).willReturn(Optional.of(like));

            // when
            likeService.unlikeMemory(1L, 10L);

            // then
            verify(likeRepository).delete(like);
        }

        @Test
        @DisplayName("좋아요가 존재하지 않으면 예외가 발생한다")
        void 좋아요가_없으면_예외발생() {
            Member member = Member.builder().id(1L).build();
            Memory memory = Memory.builder().id(10L).build();

            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));
            given(memoryRepository.findMemoryById(10L)).willReturn(Optional.of(memory));
            given(likeRepository.findByMemberAndMemory(member, memory)).willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.unlikeMemory(1L, 10L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.LIKE_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 예외가 발생한다")
        void 회원이_없으면_예외발생() {
            given(memberRepository.findMemberById(any())).willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.unlikeMemory(1L, 10L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("메모리가 존재하지 않으면 예외가 발생한다")
        void 메모리가_없으면_예외발생() {
            Member member = Member.builder().id(1L).build();
            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));
            given(memoryRepository.findMemoryById(any())).willReturn(Optional.empty());

            assertThatThrownBy(() -> likeService.unlikeMemory(1L, 10L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());
        }
    }
}
