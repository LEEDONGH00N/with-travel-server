package com.arom.with_travel.domain.accompanies.controller;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyPostRequest;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyBriefResponse;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyDetailsResponse;
import com.arom.with_travel.domain.accompanies.model.City;
import com.arom.with_travel.domain.accompanies.model.Continent;
import com.arom.with_travel.domain.accompanies.model.Country;
import com.arom.with_travel.domain.accompanies.service.AccompanyApplyService;
import com.arom.with_travel.domain.accompanies.service.AccompanyService;
import com.arom.with_travel.domain.accompanies.swagger.PostNewAccompany;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accompanies")
@Tag(name = "동행", description = "동행 api 정보")
// TODO : 로그인 작업 끝난 후 로그인 객체에서 회원 정보 가져오기
public class AccompanyController {

    private final AccompanyService accompanyService;
    private final AccompanyApplyService accompanyApplyService;

    @PostNewAccompany
    @PostMapping
    public String createNewAccompanyPost(@RequestBody AccompanyPostRequest request) {
        return accompanyService.createAccompany(request, 1L);
    }

    @GetMapping("/{accompanyId}")
    public AccompanyDetailsResponse showAccompanyDetails(@PathVariable Long accompanyId){
        return accompanyService.showAccompanyDetails(accompanyId);
    }

    @PatchMapping("/{accompanyId}/like")
    public boolean doLike(@PathVariable Long accompanyId){
        return accompanyService.toggleLike(accompanyId, 1L);
    }

    @PostMapping("/{accompanyId}/apply")
    public String doApply(@PathVariable Long accompanyId){
        return accompanyApplyService.applyAccompany(accompanyId, 1L);
    }

    @GetMapping
    public Slice<AccompanyBriefResponse> showAccompaniesBrief(
            @RequestParam(value = "country", required = false) Country country,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size){
        return accompanyService.showAccompaniesBrief(country, size, lastId);
    }
    
}
