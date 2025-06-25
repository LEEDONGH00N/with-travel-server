package com.arom.with_travel.domain.accompany.service;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyNewCommentRequest;
import com.arom.with_travel.domain.accompanies.dto.request.AccompanyUpdateCommentRequest;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyCommentSliceResponse;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyComment;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyCommentRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.service.AccompanyCommentService;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.global.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class AccompanyCommentServiceTest {

    @Mock
    private AccompanyCommentRepository accompanyCommentRepository;
    @Mock
    private AccompanyRepository accompanyRepository;
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AccompanyCommentService accompanyCommentService;

    private AccompanyNewCommentRequest request;
    private AccompanyUpdateCommentRequest update;
    private Member member;
    private Member otherMember;
    private Accompany accompany;
    private AccompanyComment comment;

    @BeforeEach
    void setUp() {
        member = AccompanyFixture.동행_작성자_생성();
        otherMember = AccompanyFixture.동행_참여자_생성();
        accompany = AccompanyFixture.동행_생성();
        accompany.post(member);
        comment = AccompanyComment.builder()
                .accompany(accompany)
                .member(member)
                .content("test comment")
                .build();
        request = new AccompanyNewCommentRequest();
        setField(request, "comment", "test comment");
        update = new AccompanyUpdateCommentRequest();
        setField(update, "comment", "update comment");
    }

    @Test
    void 댓글_등록_성공() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));

        // when
        accompanyCommentService.addCommentToAccompany(1L, 1L, request);

        // then
        verify(accompanyCommentRepository).save(any(AccompanyComment.class));
    }

    @Test
    void 댓글_등록_실패_동행없음() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accompanyCommentService.addCommentToAccompany(1L, 1L, request))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 댓글_등록_실패_유저없음() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accompanyCommentService.addCommentToAccompany(1L, 1L, request))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 댓글_조회_성공() {
        // given
        given(accompanyRepository.existsById(anyLong())).willReturn(true);

        Slice<AccompanyComment> slice = new SliceImpl<>(
                List.of(comment),
                PageRequest.of(0, 11),
                false
        );

        given(accompanyCommentRepository.findCommentsWithCursor(anyLong(), any(), any(), any()))
                .willReturn(slice);

        // when
        AccompanyCommentSliceResponse response = accompanyCommentService.getComments(1L, LocalDateTime.now(), 1L, 10);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getComments()).hasSize(1);
    }

    @Test
    void 댓글_조회_실패_동행없음() {
        // given
        given(accompanyRepository.existsById(anyLong())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> accompanyCommentService.getComments(1L, LocalDateTime.now(), 1L, 10))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 댓글_수정_성공() {
        // given
        given(accompanyCommentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        // when
        System.out.println(update.getComment());
        accompanyCommentService.updateComment(1L, member.getId(), update);

        // then
        assertThat(comment.getContent()).isEqualTo("update comment");
    }

    @Test
    void 댓글_수정_실패_댓글없음() {
        // given
        given(accompanyCommentRepository.findById(anyLong())).willReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> accompanyCommentService.updateComment(1L, member.getId(), update))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 댓글_수정_실패_작성자아님() {
        // given
        given(accompanyCommentRepository.findById(anyLong())).willReturn(Optional.of(comment));


        // when & then
        assertThatThrownBy(() -> accompanyCommentService.updateComment(1L, otherMember.getId(), update))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 댓글_삭제_성공() {
        // given
        given(accompanyCommentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        // when
        accompanyCommentService.deleteComment(1L, member.getId());

        // then
        verify(accompanyCommentRepository).delete(comment);
    }

    @Test
    void 댓글_삭제_실패_댓글없음() {
        // given
        given(accompanyCommentRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accompanyCommentService.deleteComment(1L, member.getId()))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 댓글_삭제_실패_작성자아님() {
        // given
        given(accompanyCommentRepository.findById(anyLong())).willReturn(Optional.of(comment));

        // when & then
        assertThatThrownBy(() -> accompanyCommentService.deleteComment(1L, otherMember.getId()))
                .isInstanceOf(BaseException.class);
    }
}