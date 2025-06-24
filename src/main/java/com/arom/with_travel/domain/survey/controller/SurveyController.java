// SurveyController.java
package com.arom.with_travel.domain.survey.controller;

import com.arom.with_travel.domain.survey.dto.request.SurveyRequestDto;
import com.arom.with_travel.domain.survey.dto.response.SurveyResponseDto;
import com.arom.with_travel.domain.survey.service.SurveyService;
import com.arom.with_travel.domain.survey.swagger.GetMySurveys;
import com.arom.with_travel.domain.survey.swagger.GetSingleSurvey;
import com.arom.with_travel.domain.survey.swagger.PostNewSurvey;
import com.arom.with_travel.global.jwt.service.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SurveyController {

    private final SurveyService surveyService;
    private final TokenProvider tokenProvider;

    @PostNewSurvey
    @PostMapping("/surveys")
    public void createSurvey(
            HttpServletRequest request,
            @RequestBody SurveyRequestDto dto
    ) {
        String email = tokenProvider.getMemberLoginEmail(request);
        surveyService.createSurvey(email, dto);
    }

    @GetSingleSurvey
    @GetMapping("/survey/{surveyId}")
    public SurveyResponseDto getSurvey(@PathVariable Long surveyId) {
        return surveyService.getSurvey(surveyId);
    }

    @GetMySurveys
    @GetMapping("/surveys/my")
    public List<SurveyResponseDto> getMySurveys(HttpServletRequest request) {
        String email = tokenProvider.getMemberLoginEmail(request);
        return surveyService.getSurveysByEmail(email);
    }
}
