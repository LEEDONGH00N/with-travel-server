package com.arom.with_travel.domain.member.service;

import com.arom.with_travel.domain.accompanies.dto.response.AccompanyDetailsResponse;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyApply;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyApplyRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.dto.MemberProfileRequestDto;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final AccompanyRepository accompanyRepository;
    private final AccompanyApplyRepository applyRepository;

    //내가 등록한 동행 정보들
    public List<AccompanyDetailsResponse> myPostAccompany(String email, MemberProfileRequestDto requestDto){
        Member member = memberRepository.findByEmail(email).get();

        List<Accompany> accompanies = accompanyRepository.findAccompaniesByMember(member);
        if(accompanies==null || accompanies.isEmpty()) log.warn("동행 정보 확인 불가");

        List<AccompanyDetailsResponse> response = new ArrayList<>();
        for(Accompany accompany : accompanies){
            if(!isFinish(accompany)) response.add(AccompanyDetailsResponse.from(accompany, 1L));
        }
        return response;
    }

    //내가 신청한 동행 정보들
    public List<AccompanyDetailsResponse> myApplyAccompany(String email, MemberProfileRequestDto requestDto){
        Member member = memberRepository.findByEmail(email).get();
        List<AccompanyApply> accompanyApplies = applyRepository.findAccompanyAppliesByMember(member);
        List<AccompanyDetailsResponse> response = new ArrayList<>();
        for(AccompanyApply accompanyApply : accompanyApplies){
            if(isFinish(accompanyApply.getAccompany())) continue;
            response.add(AccompanyDetailsResponse.from(accompanyApply.getAccompany(), 1L));
        }
        return response;
    }

    //지난 동행 정보들
    public List<AccompanyDetailsResponse> myPastAccompany(String email, MemberProfileRequestDto requestDto){
        Member member = memberRepository.findByEmail(email).get();
        List<Accompany> accompanies = accompanyRepository.findAccompaniesByMember(member);
        if(accompanies==null || accompanies.isEmpty()) log.warn("동행 정보 확인 불가");

        List<AccompanyDetailsResponse> response = new ArrayList<>();
        for(Accompany accompany : accompanies){
            if(isFinish(accompany)) response.add(AccompanyDetailsResponse.from(accompany, 1L));
        }
        return response;
    }

    private boolean isFinish(Accompany accompany){
        LocalDate endDate = accompany.getEndDate();
        LocalDate now = LocalDate.now();

        // true면 지난 동행임
        if(now.compareTo(endDate)>0) return true;
        return false;
    }
}
