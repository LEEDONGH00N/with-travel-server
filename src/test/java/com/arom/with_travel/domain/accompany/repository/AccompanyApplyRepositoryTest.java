package com.arom.with_travel.domain.accompany.repository;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyApply;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyApplyRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
class AccompanyApplyRepositoryTest {

    @Autowired
    private AccompanyApplyRepository accompanyApplyRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccompanyRepository accompanyRepository;

    private Member member;
    private Accompany accompany;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(Member.create("member", "member@test.com", Member.Role.USER));

        accompany = AccompanyFixture.동행_생성();
        accompany.post(member);
        accompany = accompanyRepository.save(accompany);

        for (int i = 1; i <= 3; i++) {
            AccompanyApply apply = AccompanyApply.apply(accompany, member);
            accompanyApplyRepository.save(apply);
        }
    }

    @Test
    void 사용자로부터_신청내역_조회_성공() {
        // when
        List<AccompanyApply> applies = accompanyApplyRepository.findAccompanyAppliesByMember(member);

        // then
        assertThat(applies).isNotNull();
        assertThat(applies).hasSize(3);
        assertThat(applies)
                .extracting(AccompanyApply::getMember)
                .containsOnly(member);
    }
}