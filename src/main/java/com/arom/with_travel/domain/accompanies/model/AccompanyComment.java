package com.arom.with_travel.domain.accompanies.model;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyNewCommentRequest;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.global.entity.BaseEntity;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccompanyComment extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accompany_id")
    private Accompany accompany;

    @Builder
    public AccompanyComment(String content, Member member, Accompany accompany) {
        this.content = content;
        this.member = member;
        this.accompany = accompany;
        accompany.getAccompanyComments().add(this);
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

    public void validateIsCommentWriter(Long memberId) {
        if(!member.getId().equals(memberId)){
            throw BaseException.from(ErrorCode.ACCOMPANY_COMMENT_NO_PERMISSION_UPDATE);
        }
    }

    public static AccompanyComment from(AccompanyNewCommentRequest request, Member member, Accompany accompany){
        return AccompanyComment.builder()
                .accompany(accompany)
                .content(request.getComment())
                .member(member)
                .build();
    }
}
