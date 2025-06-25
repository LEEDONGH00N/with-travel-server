package com.arom.with_travel.domain.accompanies.service;

import com.arom.with_travel.domain.accompanies.dto.response.AccompanyBriefResponse;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyApply;
import com.arom.with_travel.domain.accompanies.dto.request.AccompanyPostRequest;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyDetailsResponse;
import com.arom.with_travel.domain.accompanies.model.Country;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyApplyRepository;
import com.arom.with_travel.domain.likes.Likes;
import com.arom.with_travel.domain.likes.repository.LikesRepository;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.dto.MemberProfileRequestDto;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccompanyService {

    private final AccompanyRepository accompanyRepository;
    private final MemberRepository memberRepository;
    private final LikesRepository likesRepository;

    @Transactional
    public String createAccompany(AccompanyPostRequest request, Long memberId) {
        Member member = loadMemberOrThrow(memberId);
        Accompany accompany = Accompany.from(request);
        accompany.post(member);
        accompanyRepository.save(accompany);
        return "등록 되었습니다";
    }

    @Transactional
    public boolean toggleLike(Long accompanyId, Long memberId){
        Member member = loadMemberOrThrow(memberId);
        Accompany accompany = loadAccompanyOrThrow(accompanyId);
        Optional<Likes> like = loadLikes(accompany, member);
        if (like.isPresent()) {
            likesRepository.delete(like.get());
            return false;
        }
        Likes newLike = Likes.create(member, accompany);
        likesRepository.save(newLike);
        return true;
    }

    @Transactional
    public AccompanyDetailsResponse showAccompanyDetails(Long accompanyId){
        Accompany accompany = loadAccompanyOrThrow(accompanyId);
        accompany.addView();
        long likesCount = countLikes(accompany);
        return AccompanyDetailsResponse.from(accompany, likesCount);
    }

    @Transactional(readOnly = true)
    public Slice<AccompanyBriefResponse> showAccompaniesBrief(Country country, int size, Long lastId){
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Accompany> accompanyList = accompanyRepository.findByCountry(country, lastId, pageable);
        return accompanyList.map(AccompanyBriefResponse::from);
    }

    private Member loadMemberOrThrow(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Accompany loadAccompanyOrThrow(Long accompanyId){
        return accompanyRepository.findById(accompanyId)
                .orElseThrow(() -> BaseException.from(ErrorCode.ACCOMPANY_NOT_FOUND));
    }

    private Optional<Likes> loadLikes(Accompany accompany, Member member) {
        return likesRepository.findByAccompanyAndMember(accompany, member);
    }

    private long countLikes(Accompany accompany){
        return likesRepository.countByAccompanyId(accompany.getId());
    }
}












