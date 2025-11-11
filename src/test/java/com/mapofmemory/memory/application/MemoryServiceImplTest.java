package com.mapofmemory.memory.application;

import com.mapofmemory.global.dto.PageResponse;
import com.mapofmemory.global.exception.BusinessException;
import com.mapofmemory.global.exception.GeneralErrorCode;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
import com.mapofmemory.memory.application.dto.CreateMemoryRequest;
import com.mapofmemory.memory.application.dto.MemoryInfoResponse;
import com.mapofmemory.memory.application.dto.UpdateMemoryRequest;
import com.mapofmemory.memory.domain.Memory;
import com.mapofmemory.memory.domain.repository.MemoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemoryServiceImpl 단위 테스트")
class MemoryServiceImplTest {

    @Mock
    private MemoryRepository memoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemoryServiceImpl memoryService;

    @Nested
    @DisplayName("createMemory 메서드는")
    class CreateMemory {

        @Test
        @DisplayName("회원이 존재하면 메모리를 생성한다")
        void 메모리를_생성한다() {
            // given
            Member member = Member.builder().id(1L).nickname("테스터").build();
            CreateMemoryRequest request = new CreateMemoryRequest("제목", "내용");
            Memory savedMemory = Memory.builder().id(10L).title("제목").content("내용").member(member).build();

            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));
            given(memoryRepository.save(any(Memory.class))).willReturn(savedMemory);

            // when
            Long memoryId = memoryService.createMemory(1L, request);

            // then
            assertThat(memoryId).isEqualTo(10L);
            verify(memberRepository).findMemberById(1L);
            verify(memoryRepository).save(any(Memory.class));
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 예외가 발생한다")
        void 존재하지_않는_회원이면_예외가_발생한다() {
            // given
            given(memberRepository.findMemberById(any())).willReturn(Optional.empty());
            CreateMemoryRequest request = new CreateMemoryRequest("제목", "내용");

            // when & then
            assertThatThrownBy(() -> memoryService.createMemory(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("findMemoryById 메서드는")
    class FindMemoryById {

        @Test
        @DisplayName("메모리가 존재하면 정보를 반환한다")
        void 메모리를_조회한다() {
            // given
            Member member = Member.builder().id(1L).nickname("닉").build();
            Memory memory = Memory.builder().id(2L).title("제목").content("내용").member(member).build();
            given(memoryRepository.findMemoryById(2L)).willReturn(Optional.of(memory));

            // when
            MemoryInfoResponse response = memoryService.findMemoryById(2L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.title()).isEqualTo("제목");
            verify(memoryRepository).findMemoryById(2L);
        }

        @Test
        @DisplayName("메모리가 존재하지 않으면 예외가 발생한다")
        void 존재하지_않는_메모리면_예외발생() {
            given(memoryRepository.findMemoryById(2L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memoryService.findMemoryById(2L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("findAllByMemberId 메서드는")
    class FindAllByMemberId {

        @Test
        @DisplayName("회원의 메모리 목록을 페이지로 반환한다")
        void 메모리_목록을_반환한다() {
            // given
            Member member = Member.builder().id(1L).nickname("닉").build();
            Memory memory = Memory.builder().id(10L).title("제목").content("내용").member(member).build();

            Page<Memory> page = new PageImpl<>(List.of(memory));
            PageRequest pageable = PageRequest.of(0, 10);

            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));
            given(memoryRepository.findAllByMember(eq(member), eq(pageable))).willReturn(page);

            // when
            PageResponse<MemoryInfoResponse> response = memoryService.findAllByMemberId(1L, pageable);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.content().get(0).title()).isEqualTo("제목");
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 예외가 발생한다")
        void 회원이_없으면_예외발생() {
            given(memberRepository.findMemberById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memoryService.findAllByMemberId(1L, PageRequest.of(0, 10)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("updateMemory 메서드는")
    class UpdateMemory {

        @Test
        @DisplayName("작성자 본인이면 메모리 내용을 수정한다")
        void 메모리_내용을_수정한다() {
            // given
            Memory memory = spy(Memory.builder().id(2L).title("old").content("old").member(Member.builder().id(1L).build()).build());
            UpdateMemoryRequest request = new UpdateMemoryRequest("new", "new content");

            given(memoryRepository.findMemoryById(2L)).willReturn(Optional.of(memory));

            // when
            memoryService.updateMemory(2L, 1L, request);

            // then
            verify(memory).validateMember(1L);
            verify(memory).update("new", "new content");
        }

        @Test
        @DisplayName("존재하지 않는 메모리면 예외가 발생한다")
        void 존재하지_않는_메모리면_예외발생() {
            given(memoryRepository.findMemoryById(2L)).willReturn(Optional.empty());
            UpdateMemoryRequest request = new UpdateMemoryRequest("t", "c");

            assertThatThrownBy(() -> memoryService.updateMemory(2L, 1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteMemory 메서드는")
    class DeleteMemory {

        @Test
        @DisplayName("작성자 본인이면 메모리를 삭제한다")
        void 메모리를_삭제한다() {
            Memory memory = spy(Memory.builder().id(3L).member(Member.builder().id(1L).build()).build());
            given(memoryRepository.findMemoryById(3L)).willReturn(Optional.of(memory));

            memoryService.deleteMemory(3L, 1L);

            verify(memory).validateMember(1L);
            verify(memoryRepository).delete(memory);
        }

        @Test
        @DisplayName("존재하지 않는 메모리면 예외가 발생한다")
        void 존재하지_않는_메모리면_예외발생() {
            given(memoryRepository.findMemoryById(3L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memoryService.deleteMemory(3L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());
        }
    }
}
