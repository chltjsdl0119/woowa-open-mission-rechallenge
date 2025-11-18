package com.mapofmemory.member.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapofmemory.member.application.dto.CreateMemberRequest;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("MemberController 통합 테스트")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("회원 생성 API (POST /members)")
    class 회원_생성 {

        @Test
        @DisplayName("정상적인 요청 시 회원을 생성하고 ID를 반환한다")
        void 회원_생성_성공() throws Exception {
            // given (준비)
            CreateMemberRequest request = new CreateMemberRequest("이름", "닉네임", 25);
            String requestJson = objectMapper.writeValueAsString(request);

            // when (실행)
            ResultActions actions = mockMvc.perform(
                    post("/members")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
            ).andDo(print());

            // then (검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data").isNumber());
        }
    }

    @Nested
    @DisplayName("회원 조회 API (GET /members/{memberId})")
    class 회원_조회 {

        private Member savedMember;

        @BeforeEach
        void setUp() {
            savedMember = memberRepository.save(
                    Member.builder()
                            .name("이름")
                            .nickname("닉네임")
                            .age(26)
                            .build()
            );
        }

        @Test
        @DisplayName("존재하는 회원 ID로 조회 시 회원 정보를 반환한다")
        void 회원_조회_성공() throws Exception {
            // given (준비)
            Long memberId = savedMember.getId();

            // when (실행)
            ResultActions actions = mockMvc.perform(
                    get("/members/{memberId}", memberId)
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(print());

            // then (검증)
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(memberId))
                    .andExpect(jsonPath("$.data.name").value("이름"))
                    .andExpect(jsonPath("$.data.nickname").value("닉네임"))
                    .andExpect(jsonPath("$.data.age").value(26));
        }

        @Test
        @DisplayName("존재하지 않는 회원 ID로 조회 시 404 Not Found를 반환한다")
        void 회원_조회_실패_회원_없음() throws Exception {
            // given (준비)
            Long nonExistingId = 9999L;

            // when (실행)
            ResultActions actions = mockMvc.perform(
                    get("/members/{memberId}", nonExistingId)
                            .accept(MediaType.APPLICATION_JSON)
            ).andDo(print());

            // then (검증)
            actions.andExpect(status().isNotFound());
        }
    }
}
