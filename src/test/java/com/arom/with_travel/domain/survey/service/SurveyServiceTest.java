package com.arom.with_travel.domain.survey.service;

import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.domain.survey.Survey;
import com.arom.with_travel.domain.survey.dto.request.SurveyRequestDto;
import com.arom.with_travel.domain.survey.dto.response.SurveyResponseDto;
import com.arom.with_travel.domain.survey.repository.SurveyRepository;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@DisplayName("SurveyService 단위 테스트")
class SurveyServiceTest {

    @Mock
    SurveyRepository surveyRepository;
    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    SurveyService surveyService;

    Member member;
    SurveyRequestDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 테스트용 더미 데이터
        member = Member.create("user1", "user1@email.com", Member.Role.USER);
        dto = SurveyRequestDto.builder()
                .question("Q?")
                .answers(List.of("A1","A2"))
                .build();
    }

    @Test
    @DisplayName("createSurvey: 정상적으로 설문조사 입력 시 DB에 저장")
    void 설문_생성_성공() {
        // given: memberRepository.findByEmail()가 member를 반환하도록 stub
        given(memberRepository.findByEmail("user1@email.com"))
                .willReturn(Optional.of(member));
        // save 리턴 값은 크게 상관없음(여기선 null을 지정)
        given(surveyRepository.save(any())).willReturn(null);

        // when: 실제로 service 메서드 호출
        surveyService.createSurvey("user1@email.com", dto);

        // then: surveyRepository.save()가 정확히 한 번 호출됐는지 검증
        then(surveyRepository).should(times(1)).save(any());
    }

    @Test
    @DisplayName("createSurvey: 멤버 없으면 MEMBER_NOT_FOUND 예외")
    void 설문_생성_멤버_없음() {
        // given: memberRepository는 빈 Optional 반환
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.empty());

        // when + then: 예외가 던져지는지 검증
        assertThatThrownBy(() -> surveyService.createSurvey("noone@email.com", dto))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
    }

    @Test
    @DisplayName("getSurvey: 존재하는 surveyId면 DTO 반환")
    void 설문_조회_성공() {
        // given: Survey 엔티티와 ID 세팅
        Survey survey = Survey.create(member, "Q?", List.of("A"));
        ReflectionTestUtils.setField(survey, "id", 100L);
        given(surveyRepository.findById(100L)).willReturn(Optional.of(survey));

        // when
        SurveyResponseDto result = surveyService.getSurvey(100L);

        // then: DTO 필드가 엔티티와 일치하는지 검증
        assertThat(result.getSurveyId()).isEqualTo(100L);
        assertThat(result.getQuestion()).isEqualTo("Q?");
        assertThat(result.getAnswers()).containsExactly("A");
    }

    @Test
    @DisplayName("getSurvey: 없으면 SURVEY_NOT_FOUND 예외")
    void 설문_조회_실패() {
        // given: findById() 빈 Optional
        given(surveyRepository.findById(anyLong())).willReturn(Optional.empty());

        // when + then: SURVEY_NOT_FOUND 예외 발생
        assertThatThrownBy(() -> surveyService.getSurvey(1L))
                .isInstanceOf(BaseException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SURVEY_NOT_FOUND);
    }

    @Test
    @DisplayName("getSurveysByEmail: 멤버별 설문 리스트 반환")
    void 내_설문_목록_조회() {
        // given: 회원 조회 stub
        given(memberRepository.findByEmail("user1@email.com"))
                .willReturn(Optional.of(member));

        // given: findByMember() → 두 개의 Survey 리스트
        Survey s1 = Survey.create(member, "Q1", List.of("A"));
        Survey s2 = Survey.create(member, "Q2", List.of("B","C"));
        given(surveyRepository.findByMember(member))
                .willReturn(List.of(s1, s2));

        // when
        var list = surveyService.getSurveysByEmail("user1@email.com");

        // then: DTO 리스트 크기·내용 검증
        assertThat(list).hasSize(2)
                .extracting("question")
                .containsExactly("Q1", "Q2");
    }
}
