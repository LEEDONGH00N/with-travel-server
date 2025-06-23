package com.arom.with_travel.domain.accompanies.service;

import com.arom.with_travel.domain.accompanies.dto.event.AccompanyAppliedEvent;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyApply;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyApplyRepository;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccompanyApplyService {

    private final AccompanyRepository accompanyRepository;
    private final MemberRepository memberRepository;
    private final AccompanyApplyRepository applyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String applyAccompany(Long accompanyId, Long memberId){
        Accompany accompany = loadAccompanyOrThrow(accompanyId);
        Member proposer = loadMemberOrThrow(memberId);
        proposer.validateNotAlreadyAppliedTo(accompany);
        AccompanyAppliedEvent event = new AccompanyAppliedEvent(accompany, proposer);
        applyRepository.save(AccompanyApply.apply(accompany, proposer));
        eventPublisher.publishEvent(event);
        return "참가 신청이 완료됐습니다.";
    }

    private Member loadMemberOrThrow(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Accompany loadAccompanyOrThrow(Long accompanyId){
        return accompanyRepository.findById(accompanyId)
                .orElseThrow(() -> BaseException.from(ErrorCode.ACCOMPANY_NOT_FOUND));
    }

}
