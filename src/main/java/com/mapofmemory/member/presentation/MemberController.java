package com.mapofmemory.member.presentation;

import com.mapofmemory.global.common.CommonResponse;
import com.mapofmemory.member.application.dto.CreateMemberRequest;
import com.mapofmemory.member.application.dto.MemberInfoResponse;
import com.mapofmemory.member.domain.service.MemberService;
import com.mapofmemory.member.presentation.docs.MemberApiDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController implements MemberApiDocs {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<CommonResponse<Long>> createMember(@RequestBody CreateMemberRequest request) {
        Long memberId = memberService.createMember(request);
        return ResponseEntity.ok(CommonResponse.onSuccess(memberId));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<CommonResponse<MemberInfoResponse>> getMember(@PathVariable Long memberId) {
        MemberInfoResponse response = memberService.findMemberById(memberId);
        return ResponseEntity.ok(CommonResponse.onSuccess(response));
    }
}
