package com.arom.with_travel.domain.accompany.domain;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyApply;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.member.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SuppressWarnings("NonAsciiCharacters")
public class AccompanyApplyTest {

    private Accompany accompany;
    private Member member;

    @BeforeEach
    void setUp(){
        member = AccompanyFixture.동행_작성자_생성();
        accompany = AccompanyFixture.동행_생성();
    }

    @Test
    void 동행_신청_엔티티_생성(){
        // given & when
        AccompanyApply apply = AccompanyApply.apply(accompany, member);

        // then
        assertThat(apply.getAccompany())
                .isEqualTo(accompany);

        assertThat(apply.getMember())
                .isEqualTo(member);
    }
}
