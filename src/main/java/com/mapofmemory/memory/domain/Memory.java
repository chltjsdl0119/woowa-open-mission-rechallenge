package com.mapofmemory.memory.domain;

import com.mapofmemory.global.domain.BaseTimeEntity;
import com.mapofmemory.global.exception.BusinessException;
import com.mapofmemory.global.exception.GeneralErrorCode;
import com.mapofmemory.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memories")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Memory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validateMember(Long memberId) {
        if (!this.member.getId().equals(memberId)) {
            throw new BusinessException(GeneralErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
