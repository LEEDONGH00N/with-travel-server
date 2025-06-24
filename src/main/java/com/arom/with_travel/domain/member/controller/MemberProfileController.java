package com.arom.with_travel.domain.member.controller;

import com.arom.with_travel.domain.accompanies.dto.response.AccompanyDetailsResponse;
import com.arom.with_travel.domain.accompanies.service.AccompanyService;
import com.arom.with_travel.domain.member.dto.MemberProfileRequestDto;
import com.arom.with_travel.domain.member.dto.MemberProfileResponseDto;
import com.arom.with_travel.domain.member.service.MemberProfileService;
import com.arom.with_travel.domain.member.service.MemberService;
import com.arom.with_travel.global.jwt.service.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class MemberProfileController {

    private final TokenProvider tokenProvider;
    private final MemberProfileService memberProfileService;

    @GetMapping("/myPostAccompany")
    public List<AccompanyDetailsResponse> myPost(HttpServletRequest request, @RequestBody MemberProfileRequestDto requestDto){
        return memberProfileService.myPostAccompany(tokenProvider.getMemberLoginEmail(request),requestDto);
    }

    @GetMapping("/myApplyAccompany")
    public List<AccompanyDetailsResponse> myApply(HttpServletRequest request, @RequestBody MemberProfileRequestDto requestDto){
        return memberProfileService.myApplyAccompany(tokenProvider.getMemberLoginEmail(request),requestDto);
    }

    @GetMapping("/myPastAccompany")
    public List<AccompanyDetailsResponse> myPast(HttpServletRequest request, @RequestBody MemberProfileRequestDto requestDto){
        return memberProfileService.myPastAccompany(tokenProvider.getMemberLoginEmail(request),requestDto);
    }

}
