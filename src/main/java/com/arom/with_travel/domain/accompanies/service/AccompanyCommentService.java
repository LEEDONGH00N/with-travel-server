package com.arom.with_travel.domain.accompanies.service;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.model.AccompanyComment;
import com.arom.with_travel.domain.accompanies.dto.request.AccompanyNewCommentRequest;
import com.arom.with_travel.domain.accompanies.dto.request.AccompanyUpdateCommentRequest;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyCommentSliceResponse;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyCommentRepository;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccompanyCommentService {

    private final AccompanyCommentRepository accompanyCommentRepository;
    private final AccompanyRepository accompanyRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addCommentToAccompany(Long accompanyId, Long memberId, AccompanyNewCommentRequest request){
        Accompany accompany = loadAccompanyOrThrow(accompanyId);
        Member member = loadMemberOrThrow(memberId);
        AccompanyComment accompanyComment = AccompanyComment.from(request, member, accompany);
        accompanyCommentRepository.save(accompanyComment);
    }

    @Transactional(readOnly = true)
    public AccompanyCommentSliceResponse getComments(Long accompanyId, LocalDateTime lastCreatedAt, Long lastId, int size) {
        isAccompanyExist(accompanyId);
        Pageable pageable = PageRequest.of(0, size + 1);
        Slice<AccompanyComment> accompanyComments = accompanyCommentRepository
                .findCommentsWithCursor(accompanyId, lastCreatedAt, lastId, pageable);
        return AccompanyCommentSliceResponse.from(accompanyComments);
    }

    @Transactional
    public void updateComment(Long accompanyCommentId, Long memberId, AccompanyUpdateCommentRequest request){
        AccompanyComment accompanyComment = loadAccompanyCommentOrThrow(accompanyCommentId);
        accompanyComment.validateIsCommentWriter(memberId);
        accompanyComment.updateContent(request.getComment());
    }

    @Transactional
    public void deleteComment(Long accompanyCommentId, Long memberId){
        AccompanyComment accompanyComment = loadAccompanyCommentOrThrow(accompanyCommentId);
        accompanyComment.validateIsCommentWriter(memberId);
        accompanyCommentRepository.delete(accompanyComment);
    }

    private Member loadMemberOrThrow(Long memberId){
        return memberRepository.findById(memberId)
                .orElseThrow(() -> BaseException.from(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Accompany loadAccompanyOrThrow(Long accompanyId){
        return accompanyRepository.findById(accompanyId)
                .orElseThrow(() -> BaseException.from(ErrorCode.ACCOMPANY_NOT_FOUND));
    }

    private AccompanyComment loadAccompanyCommentOrThrow(Long accompanyCommentId){
        return accompanyCommentRepository.findById(accompanyCommentId)
                .orElseThrow(() -> BaseException.from(ErrorCode.ACCOMPANY_COMMENT_NOT_FOUND));
    }

    private void isAccompanyExist(Long accompanyId){
        if(!accompanyRepository.existsById(accompanyId)){
            throw BaseException.from(ErrorCode.ACCOMPANY_NOT_FOUND);
        }
    }
}
