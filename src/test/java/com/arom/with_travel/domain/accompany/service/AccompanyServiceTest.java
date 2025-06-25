package com.arom.with_travel.domain.accompany.service;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyPostRequest;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyBriefResponse;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyDetailsResponse;
import com.arom.with_travel.domain.accompanies.model.*;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.service.AccompanyService;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.likes.LikeFixture;
import com.arom.with_travel.domain.likes.Likes;
import com.arom.with_travel.domain.likes.repository.LikesRepository;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.global.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

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
public class AccompanyServiceTest {

    @Mock
    private AccompanyRepository accompanyRepository;
    @Mock
    private LikesRepository likesRepository;
    @Mock
    private MemberRepository memberRepository;
    @InjectMocks
    private AccompanyService accompanyService;

    private Accompany accompany;
    private AccompanyPostRequest request;
    private Member owner;
    private Member proposer;
    private Likes likes;

    @BeforeEach
    void setUp() {
        request = AccompanyFixture.동행_생성용_dto();
        owner = Member.create("test", "test@naver.com", Member.Role.USER);
        setField(owner, "nickname", "test-nickname");
        proposer = Member.create("proposer", "proposer@naver.com", Member.Role.USER);
        setField(proposer, "nickname", "proposer-nickname");
        accompany = Accompany.from(request);
        likes = LikeFixture.createLikes(owner, accompany);
    }
    @Nested
    class 동행_생성 {
        @Test
        void 동행_글쓰기_성공(){
            //given
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(owner));
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
    }

    @Nested
    class 좋아요_테스트 {
        @Test
        void 동행_좋아요_성공() {
            //given
            accompany.post(owner);
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(owner));
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
            accompany.post(owner);
            given(memberRepository.findById(anyLong())).willReturn(Optional.of(owner));
            given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
            given(likesRepository.save(any(Likes.class))).willReturn(likes);
            accompanyService.toggleLike(1L, 1L);
            given(likesRepository.findByAccompanyAndMember(accompany, owner)).willReturn(Optional.ofNullable(likes));
            //when
            boolean result = accompanyService.toggleLike(1L, 1L);
            //then
            assertThat(result).isFalse();
        }
    }

    @Nested
    class 동헹_조회{
        @Test
        void 동행_상세조회_성공(){
            //given
            accompany.post(owner);
            setField(accompany, "id", 1L);
            given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
            given(likesRepository.countByAccompanyId(anyLong())).willReturn(1L);
            //when
            AccompanyDetailsResponse result = accompanyService.showAccompanyDetails(1L);
            AccompanyDetailsResponse expected = AccompanyDetailsResponse.from(accompany, 1L);
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
            assertThatThrownBy(() -> accompanyService.showAccompanyDetails(1L))
                    .isInstanceOf(BaseException.class);
        }

        @Test
        void 동행_간략_목록_조회_성공() {
            // given
            Country country = Country.JAPAN;
            int size = 10;
            Long lastId = 5L;
            Accompany ac1 =  AccompanyFixture.동행_생성();
            ac1.post(owner);
            Accompany ac2 =  AccompanyFixture.동행_생성();
            ac2.post(owner);
            List<Accompany> accompanies = List.of(ac1, ac2);
            Slice<Accompany> accompanySlice = new SliceImpl<>(
                    accompanies,
                    PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")),
                    false
            );
            given(accompanyRepository.findByCountry(country, lastId, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"))))
                    .willReturn(accompanySlice);
            // when
            Slice<AccompanyBriefResponse> result = accompanyService.showAccompaniesBrief(country, size, lastId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            verify(accompanyRepository, times(1)).findByCountry(eq(country), eq(lastId), any(Pageable.class));
        }
        @Test
        void 동행_간략_목록_조회_빈_리스트_반환() {
            // given
            Country country = Country.JAPAN;
            int size = 10;
            Long lastId = 5L;
            List<Accompany> accompanies = List.of();
            Slice<Accompany> accompanySlice = new SliceImpl<>(
                    accompanies,
                    PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")),
                    false
            );
            given(accompanyRepository.findByCountry(country, lastId, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"))))
                    .willReturn(accompanySlice);
            // when
            Slice<AccompanyBriefResponse> result = accompanyService.showAccompaniesBrief(country, size, lastId);
            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            verify(accompanyRepository, times(1)).findByCountry(eq(country), eq(lastId), any(Pageable.class));
        }
    }

}
