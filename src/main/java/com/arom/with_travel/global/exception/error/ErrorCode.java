package com.arom.with_travel.global.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TMP_ERROR("S3-0000", "파일 형식이 올바르지 않습니다.", ErrorDisplayType.MODAL),

    //client error : 4xx

    //member
    MEMBER_NOT_FOUND("MEM-0000", "해당 회원이 존재하지 않습니다.", ErrorDisplayType.POPUP),
    MEMBER_ALREADY_REGISTERED("MEM-0001", "이미 회원가입되어 있습니다.", ErrorDisplayType.POPUP),

    //token
    TOKEN_NOT_FOUND("TKN-0000", "refresh token이 존재하지 않습니다.", ErrorDisplayType.POPUP),
    INVALID_TOKEN("TKN-0001", "유효하지 않은 token입니다.", ErrorDisplayType.POPUP),

    //accompany
    ACCOMPANY_NOT_FOUND("ACC-0000", "해당 동행이 존재하지 않습니다.", ErrorDisplayType.POPUP),
    ACCOMPANY_ALREADY_APPLIED("ACC-0001", "이미 신청한 동행입니다.", ErrorDisplayType.POPUP),
    ACCOMPANY_POST_ERROR("ACC-0002", "동행 입력이 올바르지 않습니다.", ErrorDisplayType.POPUP),
    ACCOMPANY_ALREADY_LIKED("ACC-0003", "좋아요를 이미 눌렀습니다.", ErrorDisplayType.POPUP),
    ACCOMPANY_COMMENT_NOT_FOUND("ACC-0004", "해당 동행 댓글을 찾을 수 없습니다.", ErrorDisplayType.POPUP),
    ACCOMPANY_COMMENT_NO_PERMISSION_UPDATE("ACC-0005", "댓글을 수정할 수 없습니다.", ErrorDisplayType.POPUP),
    // ACCOMPANY_ALREADY_CONFIRMED("ACC-0002", "참가 확정된 동행입니다.", ErrorDisplayType.POPUP)
    ACCOMPANY_LIKES_UNABLE_DECREASE("ACC-0006", "좋아요 수가 0보다 작습니다.", ErrorDisplayType.POPUP),

    //survey
    SURVEY_NOT_FOUND("SVY-0000", "해당 설문이 존재하지 않습니다.", ErrorDisplayType.POPUP),
    INVALID_SURVEY_ANSWER("SVY-0001", "설문 답변이 비어있습니다.", ErrorDisplayType.POPUP),
    OVER_ANSWER_LIMIT("SVY-0002", "답변 개수가 초과되었습니다..", ErrorDisplayType.POPUP),
    INVALID_SURVEY_QUESTION("SVY-0003", "설문 질문이 비어있습니다.", ErrorDisplayType.POPUP),
    ;


    private final String code;
    private final String message;
    private final ErrorDisplayType displayType;
}
