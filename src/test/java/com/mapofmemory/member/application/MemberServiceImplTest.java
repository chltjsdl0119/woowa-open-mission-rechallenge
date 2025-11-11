package com.mapofmemory.member.application;

import com.mapofmemory.global.exception.BusinessException;
import com.mapofmemory.global.exception.GeneralErrorCode;
import com.mapofmemory.member.application.dto.CreateMemberRequest;
import com.mapofmemory.member.application.dto.MemberInfoResponse;
import com.mapofmemory.member.domain.Member;
import com.mapofmemory.member.domain.repository.MemberRepository;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("MemberServiceImpl 단위 테스트")
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Nested
    @DisplayName("createMember()는")
    class Describe_createMember {

        @Test
        @DisplayName("닉네임이 중복되지 않으면 회원을 저장하고 ID를 반환한다")
        void 닉네임_중복_없음_회원_저장_성공() {
            // given
            CreateMemberRequest request = new CreateMemberRequest("홍길동", "길동이", 25);
            Member savedMember = Member.builder()
                    .id(1L)
                    .name("홍길동")
                    .nickname("길동이")
                    .age(25)
                    .build();

            given(memberRepository.existsByNickname("길동이")).willReturn(false);
            given(memberRepository.save(org.mockito.ArgumentMatchers.any(Member.class))).willReturn(savedMember);

            // when
            Long memberId = memberService.createMember(request);

            // then
            assertThat(memberId).isEqualTo(1L);
            verify(memberRepository).save(org.mockito.ArgumentMatchers.any(Member.class));
        }

        @Test
        @DisplayName("닉네임이 중복되면 BusinessException을 던진다")
        void 닉네임_중복시_예외_발생() {
            // given
            CreateMemberRequest request = new CreateMemberRequest("홍길동", "길동이", 25);
            given(memberRepository.existsByNickname("길동이")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.DUPLICATE_NICKNAME.getMessage());
        }
    }

    @Nested
    @DisplayName("findMemberById()는")
    class Describe_findMemberById {

        @Test
        @DisplayName("회원이 존재하면 MemberInfoResponse를 반환한다")
        void 회원_존재시_정보_반환() {
            // given
            Member member = Member.builder()
                    .id(1L)
                    .name("홍길동")
                    .nickname("길동이")
                    .age(25)
                    .build();

            given(memberRepository.findMemberById(1L)).willReturn(Optional.of(member));

            // when
            MemberInfoResponse response = memberService.findMemberById(1L);

            // then
            assertThat(response.name()).isEqualTo("홍길동");
            assertThat(response.nickname()).isEqualTo("길동이");
            assertThat(response.age()).isEqualTo(25);
        }

        @Test
        @DisplayName("회원이 존재하지 않으면 BusinessException을 던진다")
        void 회원_없음시_예외_발생() {
            // given
            given(memberRepository.findMemberById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> memberService.findMemberById(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(GeneralErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
    }
}
