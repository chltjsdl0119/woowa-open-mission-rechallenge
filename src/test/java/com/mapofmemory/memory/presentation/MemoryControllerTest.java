package com.mapofmemory.memory.presentation;

import com.mapofmemory.like.domain.service.LikeService;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
import com.mapofmemory.memory.domain.Memory;
import com.mapofmemory.memory.domain.repository.MemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("MemoryController 통합 테스트")
class MemoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LikeService likeService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemoryRepository memoryRepository;

    private Member testMember;
    private Member otherMember;
    private Memory testMemory;

    @BeforeEach
    void setUp() {
        testMember = memberRepository.save(
                Member.builder().name("테스트유저").nickname("테스터").age(25).build()
        );
        otherMember = memberRepository.save(
                Member.builder().name("다른유저").nickname("해커").age(30).build()
        );
        testMemory = memoryRepository.save(
                Memory.builder()
                        .title("테스트 제목")
                        .content("테스트용 이야기 내용")
                        .member(testMember)
                        .build()
        );
    }

    @Nested
    @DisplayName("이야기 작성 API (POST /memories)")
    class 이야기_작성 {

        @Test
        @DisplayName("v3.2 가짜 인증: 정상적인 요청 시 이야기를 생성하고 ID를 반환한다")
        void 이야기_작성_성공() throws Exception {
            // given
            String requestJson = """
                    {
                        "title": "새로 작성된 제목",
                        "content": "새로 작성된 이야기"
                    }
                    """;

            // when
            ResultActions actions = mockMvc.perform(
                    post("/memories")
                            .param("memberId", String.valueOf(testMember.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andDo(print());

            // then (CommonResponse<Long> 검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isNumber()); // 새로 생성된 Memory ID
        }
    }

    @Nested
    @DisplayName("이야기 단건 조회 API (GET /memories/{memoryId})")
    class 이야기_단건_조회 {

        @Test
        @DisplayName("존재하는 이야기 ID로 조회 시 이야기 정보를 반환한다")
        void 이야기_조회_성공() throws Exception {
            // given
            Long memoryId = testMemory.getId();

            // when
            ResultActions actions = mockMvc.perform(
                    get("/memories/{memoryId}", memoryId)
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(print());

            // then (CommonResponse<MemoryInfoResponse> 검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(memoryId))
                    .andExpect(jsonPath("$.data.title").value("테스트 제목"))
                    .andExpect(jsonPath("$.data.content").value("테스트용 이야기 내용"));
        }
    }

    @Nested
    @DisplayName("이야기 목록 조회 API (GET /memories)")
    class 이야기_목록_조회 {

        @Test
        @DisplayName("v3.2 가짜 인증: 특정 회원이 작성한 이야기 목록을 페이징하여 반환한다")
        void 이야기_목록_조회_성공() throws Exception {
            // given
            memoryRepository.save(Memory.builder().title("다른 유저 제목").content("다른 유저 글").member(otherMember).build());

            // when
            ResultActions actions = mockMvc.perform(
                    get("/memories")
                            .param("memberId", String.valueOf(testMember.getId()))
                            .param("page", "0")
                            .param("size", "10")
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(print());

            // then (CommonResponse<PageResponse<...>> 검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.content[0].id").value(testMemory.getId()));
        }
    }


    @Nested
    @DisplayName("이야기 수정 API (PUT /memories/{memoryId})")
    class 이야기_수정 {

        @Test
        @DisplayName("v3.2 가짜 인증: 자신의 이야기를 성공적으로 수정한다")
        void 이야기_수정_성공() throws Exception {
            // given
            String requestJson = """
                    {
                        "title": "수정된 제목",
                        "content": "수정된 이야기 내용"
                    }
                    """;

            // when
            ResultActions actions = mockMvc.perform(
                    put("/memories/{memoryId}", testMemory.getId())
                            .param("memberId", String.valueOf(testMember.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andDo(print());

            // then (CommonResponse<MemoryInfoResponse> 검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(testMemory.getId()))
                    .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                    .andExpect(jsonPath("$.data.content").value("수정된 이야기 내용"));
        }

        @Test
        @DisplayName("v3.2 가짜 인증: 남의 이야기를 수정하려 하면 Forbidden 에러가 발생한다")
        void 이야기_수정_실패_권한없음() throws Exception {
            // given
            String requestJson = """
                    {
                        "title": "해킹 시도",
                        "content": "해킹 시도"
                    }
                    """;

            // when
            ResultActions actions = mockMvc.perform(
                    put("/memories/{memoryId}", testMemory.getId())
                            .param("memberId", String.valueOf(otherMember.getId()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andDo(print());

            // then
            actions.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("이야기 삭제 API (DELETE /memories/{memoryId})")
    class 이야기_삭제 {

        @Test
        @DisplayName("v3.2 가짜 인증: 자신의 이야기를 성공적으로 삭제한다")
        void 이야기_삭제_성공() throws Exception {
            // when
            ResultActions actions = mockMvc.perform(
                    delete("/memories/{memoryId}", testMemory.getId())
                            .param("memberId", String.valueOf(testMember.getId()))
            ).andDo(print());

            // then (CommonResponse<Void> 검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").doesNotExist());
        }

        @Test
        @DisplayName("v3.2 가짜 인증: 남의 이야기를 삭제하려 하면 Forbidden 에러가 발생한다")
        void 이야기_삭제_실패_권한없음() throws Exception {
            // when
            ResultActions actions = mockMvc.perform(
                    delete("/memories/{memoryId}", testMemory.getId())
                            .param("memberId", String.valueOf(otherMember.getId()))
            ).andDo(print());

            // then
            actions.andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("이야기 좋아요 API (POST /memories/{memoryId}/like)")
    class 이야기_좋아요 {

        @Test
        @DisplayName("v3.2 가짜 인증: 회원이 이야기를 성공적으로 '좋아요'한다")
        void 이야기_좋아요_성공() throws Exception {
            // when
            ResultActions actions = mockMvc.perform(
                    post("/memories/{memoryId}/like", testMemory.getId())
                            .param("memberId", String.valueOf(testMember.getId()))
            ).andDo(print());

            // then (CommonResponse<Long> 검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isNumber());
        }
    }

    @Nested
    @DisplayName("이야기 좋아요 취소 API (DELETE /memories/{memoryId}/like)")
    class 이야기_좋아요_취소 {

        @BeforeEach
        void setUp() {
            // '좋아요'를 미리 눌러놔야 '취소' 테스트 가능
            likeService.likeMemory(testMember.getId(), testMemory.getId());
        }

        @Test
        @DisplayName("v3.2 가짜 인증: 회원이 '좋아요'를 성공적으로 취소한다")
        void 이야기_좋아요_취소_성공() throws Exception {
            // when
            ResultActions actions = mockMvc.perform(
                    delete("/memories/{memoryId}/like", testMemory.getId())
                            .param("memberId", String.valueOf(testMember.getId()))
            ).andDo(print());

            // then (CommonResponse<Void> 검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").doesNotExist());
        }
    }
}
