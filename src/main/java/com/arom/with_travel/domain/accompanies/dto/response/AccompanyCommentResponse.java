package com.arom.with_travel.domain.accompanies.dto.response;

import com.arom.with_travel.domain.accompanies.model.AccompanyComment;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class AccompanyCommentResponse {
    private Long commentId;
    private String memberId;
    private String nickname;
    private String comment;

    public static AccompanyCommentResponse from(AccompanyComment accompanyComment){
        return AccompanyCommentResponse.builder()
                .commentId(accompanyComment.getId())
                .comment(accompanyComment.getContent())
                .memberId(accompanyComment.getMember().getOauthId())
                .nickname(accompanyComment.getMember().getNickname())
                .build();
    }

    public static List<AccompanyCommentResponse> fromEntities(List<AccompanyComment> accompanyComments) {
        return accompanyComments.stream()
                .map(AccompanyCommentResponse::from)
                .collect(Collectors.toList());
    }
}
