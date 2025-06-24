package com.arom.with_travel.domain.survey.domain;

import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.survey.Survey;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
public class SurveyTest {

    @Test
    @DisplayName("정상 입력으로 Survey.create 호출 시 엔티티가 생성된다.")
    void 설문_엔티티_생성() {
        // given: 유효한 질문·답변 리스트와 더미 회원
        Member member = Member.create("user1", "user1@test.com", Member.Role.USER);
        String question = "최애 여행지는?";
        List<String> answers = List.of("제주도", "파리");

        // when
        Survey survey = Survey.create(member, question, answers);

        // then
        assertThat(survey.getMember()).isEqualTo(member);
        assertThat(survey.getQuestion()).isEqualTo(question);
        assertThat(survey.getAnswers()).containsExactlyElementsOf(answers);
    }

    @Test
    @DisplayName("질문이 비었거나 Null이면 INVALID_SURVEY_QUESTION 예외")
    void 질문_누락시_예외() {
        Member member = Member.create("user1", "user1@test.com", Member.Role.USER);
        List<String> answers = List.of("제주도", "파리");

        assertThatThrownBy(() -> Survey.create(member, null, answers))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_SURVEY_QUESTION);

        assertThatThrownBy(() -> Survey.create(member, "   ", answers))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_SURVEY_QUESTION);
    }

    @Test
    @DisplayName("답변이 비었거나 Null이면 INVALID_SURVEY_ANSWER 예외")
    void 답변_빈_리스트_예외() {
        Member member = Member.create("user1", "user1@test.com", Member.Role.USER);
        String question = "최애 여행지는?";

        assertThatThrownBy(() -> Survey.create(member, question, null))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_SURVEY_ANSWER);

        assertThatThrownBy(() -> Survey.create(member, question, List.of()))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_SURVEY_ANSWER);
    }
}
