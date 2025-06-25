package com.arom.with_travel.domain.accompany.service;

import com.arom.with_travel.domain.accompanies.dto.event.AccompanyAppliedEvent;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyApply;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyApplyRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.service.AccompanyApplyService;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
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

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
public class AccompanyApplyServiceTest {
    @InjectMocks
    private AccompanyApplyService accompanyApplyService;

    @Mock
    private AccompanyRepository accompanyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AccompanyApplyRepository applyRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private Accompany accompany;
    private Member owner;
    private Member proposer;

    @BeforeEach
    void setUp() {
        accompany = AccompanyFixture.동행_생성();
        owner = AccompanyFixture.동행_작성자_생성();
        proposer = AccompanyFixture.동행_참여자_생성();
        accompany.post(owner);
    }

    @Test
    void 동행_신청_성공() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(memberRepository.findById(anyLong())).willReturn(Optional.of(proposer));

        // when
        String result = accompanyApplyService.applyAccompany(1L, 1L);

        // then
        assertThat(result).isEqualTo("참가 신청이 완료됐습니다.");

        verify(applyRepository).save(any(AccompanyApply.class));
        verify(eventPublisher).publishEvent(any(AccompanyAppliedEvent.class));
    }

    @Test
    void 동행_이미_신청_실패(){
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.empty());
        AccompanyApply apply = AccompanyApply.apply(accompany, proposer);
        proposer.getAccompanyApplies().add(apply);

        // when & then
        assertThatThrownBy(() -> accompanyApplyService.applyAccompany(1L, 1L))
                .isInstanceOf(BaseException.class);
    }
}
