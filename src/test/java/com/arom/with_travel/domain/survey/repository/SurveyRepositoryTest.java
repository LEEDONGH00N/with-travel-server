package com.arom.with_travel.domain.survey.repository;

import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.domain.survey.Survey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("SurveyRepository 통합 테스트")
class SurveyRepositoryTest {

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("Survey와 answers 컬렉션이 DB에 저장·조회된다")
    void 설문_저장_및_조회() {
        // given: Member 저장
        Member member = Member.create("user1", "user1@test.com", Member.Role.USER);
        memberRepository.save(member);

        // when: Survey 저장
        Survey saved = Survey.create(member, "최애 여행지는?", List.of("제주도", "파리"));
        surveyRepository.save(saved);

        // then: 다시 조회해서 필드·answers 비교
        Survey loaded = surveyRepository.findById(saved.getId()).orElseThrow();
        assertThat(loaded.getQuestion()).isEqualTo("최애 여행지는?");
        assertThat(loaded.getAnswers())
                .hasSize(2)
                .containsExactly("제주도", "파리");
    }
}
