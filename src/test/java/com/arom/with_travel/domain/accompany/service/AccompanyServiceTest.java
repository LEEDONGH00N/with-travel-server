package com.arom.with_travel.domain.accompany.service;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyPostRequest;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyDetailsResponse;
import com.arom.with_travel.domain.accompanies.model.*;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyApplyRepository;
import com.arom.with_travel.domain.accompanies.service.AccompanyService;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.likes.LikeFixture;
import com.arom.with_travel.domain.likes.Likes;
import com.arom.with_travel.domain.likes.repository.LikesRepository;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.global.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
public class AccompanyServiceTest {

    @Mock
    private AccompanyRepository accompanyRepository;
    @Mock
    private LikesRepository likesRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private AccompanyApplyRepository applyRepository;

    @InjectMocks
    private AccompanyService accompanyService;

    private Accompany accompany;
    private AccompanyPostRequest request;
    private Member member;
    private Member proposer;
    private Likes likes;

    @BeforeEach
    public void setUp() {
        request = AccompanyFixture.동행_생성용_dto();
        member = Member.create("test", "test@naver.com", Member.Role.USER);
        setField(member, "nickname", "test-nickname");
        proposer = Member.create("proposer", "proposer@naver.com", Member.Role.USER);
        setField(proposer, "nickname", "proposer-nickname");
        accompany = Accompany.from(request);
        likes = LikeFixture.createLikes(member, accompany);
    }

    @Test
    void 동행_글쓰기_성공(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(accompanyRepository.save(any(Accompany.class))).willReturn(accompany);

        //when
        String result = accompanyService.createAccompany(request, 1L);

        //then
        assertThat(result).isEqualTo("등록 되었습니다");
        verify(accompanyRepository, times(1)).save(any(Accompany.class));
    }

    @Test
    void 동행_글쓰기_맴버찾을수없음_실패(){
        //given
        given(memberRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> accompanyService.createAccompany(request, 1L))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 동행_좋아요_성공(){
        //given
        accompany.post(member);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(likesRepository.save(any(Likes.class))).willReturn(likes);

        //when
        boolean result = accompanyService.toggleLike(1L, 1L);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void 동행_좋아요_취소_성공() {
        //given
        accompany.post(member);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(likesRepository.save(any(Likes.class))).willReturn(likes);
        accompanyService.toggleLike(1L, 1L); // 이미 좋아요를 누른 게시물
        given(likesRepository.findByAccompanyAndMember(accompany, member)).willReturn(Optional.ofNullable(likes));

        //when
        boolean result = accompanyService.toggleLike(1L, 1L);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void 동행_좋아요_취소_0미만_실패(){
        //given
        accompany.post(member);
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(likesRepository.findByAccompanyAndMember(accompany, member)).willReturn(Optional.ofNullable(likes));

        //when & then
        assertThatThrownBy(() -> accompanyService.toggleLike(1L, 1L))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 동행_상세조회_성공(){
        //given
        accompany.post(member);
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));

        //when
        AccompanyDetailsResponse result = accompanyService.showDetails(1L);
        AccompanyDetailsResponse expected = AccompanyDetailsResponse.from(accompany, 0);
        setField(expected, "views", 1L);

        //then
        assertThat(result).isNotNull();
        assertThat(result).isInstanceOf(AccompanyDetailsResponse.class);
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 동행_상세조회_실패(){
        //given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> accompanyService.showDetails(1L))
                .isInstanceOf(BaseException.class);
    }

//    @Test
//    void 동행_신청_성공(){
//        //given
//        accompany.post(member);
//        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
//        given(memberRepository.findById(anyLong())).willReturn(Optional.of(proposer));
//
//        //when
//        String result = accompanyService.applyAccompany(1L, 1L);
//
//        //then
//        assertThat(result).isEqualTo("참가 신청이 완료됐습니다.");
//    }
}
