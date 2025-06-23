package com.arom.with_travel.domain.accompany.domain;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyPostRequest;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.global.exception.BaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SuppressWarnings("NonAsciiCharacters")
public class AccompanyTest {

    private Member owner;
    private Member participant;
    private AccompanyPostRequest accompanyPostRequest;

    @BeforeEach
    void setUp() {
        owner = AccompanyFixture.동행_작성자_생성();
        participant = AccompanyFixture.동행_참여자_생성();
        accompanyPostRequest = AccompanyFixture.동행_생성용_dto();
    }

    @Test
    void 요청으로부터_동행_엔티티를_생성() {
        Accompany accompany = Accompany.from(accompanyPostRequest);
        assertThat(accompanyPostRequest)
                .usingRecursiveComparison()
                .ignoringFields(
                        "id", "views", "member",
                        "likes", "accompanyReviews", "accompanyApplies",
                        "images", "accompanyComments", "createdAt", "updatedAt"
                )
                .isEqualTo(accompany);
    }

    @Test
    void 동행_작성자의_동행_작성(){
        //given
        Accompany accompany = Accompany.from(accompanyPostRequest);

        //when
        accompany.post(owner);

        //then
        assertThat(accompany.getMember())
                .isEqualTo(owner);
    }

    @Test
    void 조회수_증가(){
        //given
        Accompany accompany = Accompany.from(accompanyPostRequest);

        //when
        accompany.addView();

        // then
        assertThat(accompany.getViews())
                .isEqualTo(1L);
    }

    @Test
    void 작성자_id_조회(){
        //given
        Accompany accompany = Accompany.from(accompanyPostRequest);
        accompany.post(owner);
        //when
        Long ownerId = accompany.getOwnerId();

        assertThat(ownerId)
                .isEqualTo(owner.getId());
    }
}
