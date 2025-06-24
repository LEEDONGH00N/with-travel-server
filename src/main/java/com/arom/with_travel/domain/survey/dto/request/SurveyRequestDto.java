package com.arom.with_travel.domain.survey.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor   // <— 모든 필드를 초기화하는 생성자 추가
@Builder
public class SurveyRequestDto {

    @NotBlank(message = "질문은 비어 있을 수 없습니다.")
    private String question;

    @NotEmpty(message = "설문 답변은 최소 1개 이상이어야 합니다.")
    private List<String> answers;
}
