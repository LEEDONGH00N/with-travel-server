// SurveyService.java
package com.arom.with_travel.domain.survey.service;

import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.domain.survey.Survey;
import com.arom.with_travel.domain.survey.dto.request.SurveyRequestDto;
import com.arom.with_travel.domain.survey.dto.response.SurveyResponseDto;
import com.arom.with_travel.domain.survey.repository.SurveyRepository;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createSurvey(String email, SurveyRequestDto dto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

        Survey survey = Survey.create(member, dto.getQuestion(), dto.getAnswers());
        surveyRepository.save(survey);
    }

    @Transactional(readOnly = true)
    public SurveyResponseDto getSurvey(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> BaseException.from(ErrorCode.SURVEY_NOT_FOUND));
        return SurveyResponseDto.from(survey);
    }

    @Transactional(readOnly = true)
    public List<SurveyResponseDto> getSurveysByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));

        return surveyRepository.findByMember(member).stream()
                .map(SurveyResponseDto::from)
                .toList();
    }
}
