package com.mapofmemory.memory.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapofmemory.global.common.PageResponse;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

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

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MemoryServiceImpl memoryService;

    @Nested
    @DisplayName("createMemory 메서드는")
    class CreateMemory {

        @Test
        @DisplayName("회원이 존재하면 기억을 생성한다")
        void 메모리를_생성한다() {
            // given
            Member member = Member.builder().id(1L).name("이름").nickname("닉네임").age(26).build();
            CreateMemoryRequest request = new CreateMemoryRequest("제목", "내용", 37.1234, 127.5678);
            Memory savedMemory = Memory.builder()
                    .id(10L)
                    .title(request.title())
                    .content(request.content())
                    .member(member)
                    .latitude(request.latitude())
                    .longitude(request.longitude())
                    .build();

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
            CreateMemoryRequest request = new CreateMemoryRequest("제목", "내용", 37.1234, 127.5678);

            // when & then
            assertThatThrownBy(() -> memoryService.createMemory(1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("findMemoryById 메서드는")
    class FindMemoryInfoById {

        @Test
        @DisplayName("캐시에 데이터가 없으면 DB를 호출 후 결과를 캐시하고 DTO를 반환한다 (Cache Miss)")
        void 캐시_미스_시_DB를_호출한다() {
            // given
            Member testMember = Member.builder().id(1L).name("이름").nickname("닉네임").age(26).build();
            Memory testMemory = Memory.builder()
                    .id(1L)
                    .title("제목")
                    .content("내용")
                    .member(testMember)
                    .latitude(37.1234)
                    .longitude(127.5678)
                    .build();

            String cacheKey = "memory:1";

            // ★ opsForValue() 반환값 Stubbing 추가
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            when(valueOperations.get(cacheKey)).thenReturn(null);
            when(memoryRepository.findById(1L)).thenReturn(Optional.of(testMemory));

            // when
            MemoryInfoResponse response = memoryService.findMemoryById(1L);

            // then
            assertThat(response).isNotNull();
            assertThat(response.title()).isEqualTo("제목");

            verify(memoryRepository, times(1)).findById(1L);
            verify(valueOperations, times(1)).set(eq(cacheKey), any(), anyLong(), any());
        }

        @Test
        @DisplayName("두 번째 호출 시, DB를 거치지 않고 캐시 데이터를 반환한다 (Cache Hit)")
        void 캐시_히트_시_DB_호출을_건너뛴다() {
            // given
            String cacheKey = "memory:2";

            MemoryInfoResponse expectedResponse = new MemoryInfoResponse(
                    2L, "제목", "내용"
            );

            // ★ opsForValue() 반환값 Stubbing 추가
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            when(valueOperations.get(cacheKey)).thenReturn(expectedResponse);
            when(objectMapper.convertValue(any(), eq(MemoryInfoResponse.class))).thenReturn(expectedResponse);

            // when
            MemoryInfoResponse response = memoryService.findMemoryById(2L);

            // then
            assertThat(response).isNotNull();
            verify(memoryRepository, never()).findById(2L);
            verify(valueOperations, times(1)).get(cacheKey);
        }

        @Test
        @DisplayName("기억이 존재하지 않으면 예외가 발생한다 (Cache Miss)")
        void 존재하지_않는_메모리면_예외발생() {
            // given
            String cacheKey = "memory:2";

            // ★ opsForValue() 반환값 Stubbing 추가
            when(redisTemplate.opsForValue()).thenReturn(valueOperations);

            when(valueOperations.get(cacheKey)).thenReturn(null);
            when(memoryRepository.findById(2L)).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> memoryService.findMemoryById(2L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());

            verify(memoryRepository, times(1)).findById(2L);
        }
    }

    @Nested
    @DisplayName("findAllByMemberId 메서드는")
    class FindAllByMemberId {

        @Test
        @DisplayName("회원의 기억 목록을 페이지로 반환한다")
        void 메모리_목록을_반환한다() {
            // given
            Member member = Member.builder().id(1L).nickname("닉네임").build();
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
    @DisplayName("findMemoriesInMap 메서드는")
    class FindMemoriesInMap {
        @Test
        @DisplayName("지도 범위 내의 기억 조회 성공 - 서울 시청 기준")
        void findMemoriesInMap_seoulRange_success() {
            // Given: 서울 시청 기준
            double lat = 37.5665;
            double lng = 126.9780;
            double range = 0.05;

            double minLat = lat - range;
            double maxLat = lat + range;
            double minLng = lng - range;
            double maxLng = lng + range;

            Member member = Member.builder().id(1L).build();

            Memory memory1 = Memory.builder()
                    .id(1L)
                    .title("서울 기억 1")
                    .content("광화문 근처")
                    .latitude(37.5700)
                    .longitude(126.9768)
                    .member(member)
                    .build();

            Memory memory2 = Memory.builder()
                    .id(2L)
                    .title("서울 기억 2")
                    .content("종로 근처")
                    .latitude(37.5680)
                    .longitude(126.9820)
                    .member(member)
                    .build();

            when(memoryRepository.findAllInMapWithIndexHint(
                    minLat, maxLat, minLng, maxLng
            )).thenReturn(List.of(memory1, memory2));

            // When
            List<MemoryInfoResponse> responses =
                    memoryService.findMemoriesInMap(lat, lng, range);

            // Then
            assertThat(responses).hasSize(2);

            assertThat(responses.get(0).title()).isEqualTo("서울 기억 1");
            assertThat(responses.get(1).title()).isEqualTo("서울 기억 2");
        }
    }

    @Nested
    @DisplayName("updateMemory 메서드는")
    class UpdateMemory {

        @Test
        @DisplayName("작성자 본인이면 기억 내용을 수정한다")
        void 메모리_내용을_수정한다() {
            // given
            Memory memory = spy(Memory.builder().id(2L).title("이전의 제목").content("이전의 내용").member(Member.builder().id(1L).build()).build());
            UpdateMemoryRequest request = new UpdateMemoryRequest("새로운 제목", "새로운 내용");

            given(memoryRepository.findMemoryById(2L)).willReturn(Optional.of(memory));

            // when
            memoryService.updateMemory(2L, 1L, request);

            // then
            verify(memory).validateMember(1L);
            verify(memory).update("새로운 제목", "새로운 내용");
        }

        @Test
        @DisplayName("존재하지 않는 기억이면 예외가 발생한다")
        void 존재하지_않는_메모리면_예외발생() {
            given(memoryRepository.findMemoryById(2L)).willReturn(Optional.empty());
            UpdateMemoryRequest request = new UpdateMemoryRequest("새로운 제목", "새로운 내용");

            assertThatThrownBy(() -> memoryService.updateMemory(2L, 1L, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("deleteMemory 메서드는")
    class DeleteMemory {

        @Test
        @DisplayName("작성자 본인이면 기억을 삭제한다")
        void 메모리를_삭제한다() {
            Memory memory = spy(Memory.builder().id(3L).member(Member.builder().id(1L).build()).build());
            given(memoryRepository.findMemoryById(3L)).willReturn(Optional.of(memory));

            memoryService.deleteMemory(3L, 1L);

            verify(memory).validateMember(1L);
            verify(memoryRepository).delete(memory);
        }

        @Test
        @DisplayName("존재하지 않는 기억이면 예외가 발생한다")
        void 존재하지_않는_메모리면_예외발생() {
            given(memoryRepository.findMemoryById(3L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memoryService.deleteMemory(3L, 1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMORY_NOT_FOUND.getMessage());
        }
    }
}
