package com.arom.with_travel.domain.survey.dto.response;

import com.arom.with_travel.domain.survey.Survey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponseDto {
    private Long surveyId;
    private String question;
    private List<String> answers;

    public static SurveyResponseDto from(Survey survey) {
        return SurveyResponseDto.builder()
                .surveyId(survey.getId())
                .question(survey.getQuestion())
                .answers(survey.getAnswers())
                .build();
    }
}
