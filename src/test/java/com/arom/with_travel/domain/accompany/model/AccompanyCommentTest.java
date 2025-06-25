package com.arom.with_travel.domain.accompany.model;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyNewCommentRequest;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyComment;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.global.exception.BaseException;
import com.arom.with_travel.global.exception.error.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@SuppressWarnings("NonAsciiCharacters")
public class AccompanyCommentTest {
    private Member writer;
    private Accompany accompany;

    @BeforeEach
    void setUp() {
        writer = Member.create("writer", "writer@test.com", Member.Role.USER);
        setField(writer, "id", 1L);
        accompany = AccompanyFixture.동행_생성();
        accompany.post(writer);
    }

    @Test
    void 댓글_생성_성공() {
        // given
        AccompanyNewCommentRequest request = new AccompanyNewCommentRequest();
        setField(request, "comment", "테스트 댓글");
        // when
        AccompanyComment comment = AccompanyComment.from(request, writer, accompany);
        // then
        assertThat(comment.getContent()).isEqualTo("테스트 댓글");
        assertThat(comment.getMember()).isEqualTo(writer);
        assertThat(comment.getAccompany()).isEqualTo(accompany);
    }

    @Test
    void 댓글_내용_수정_성공() {
        // given
        AccompanyComment comment = AccompanyComment.builder()
                .content("기존 댓글")
                .member(writer)
                .accompany(accompany)
                .build();
        // when
        comment.updateContent("수정된 댓글");
        // then
        assertThat(comment.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    void 댓글_작성자_검증_성공() {
        // given
        AccompanyComment comment = AccompanyComment.builder()
                .content("댓글")
                .member(writer)
                .accompany(accompany)
                .build();
        // when, then
        comment.validateIsCommentWriter(writer.getId());
    }

    @Test
    void 댓글_작성자_검증_실패() {
        // given
        Member otherMember = Member.create("다른회원", "other@test.com", Member.Role.USER);
        setField(otherMember, "id", 2L);
        AccompanyComment comment = AccompanyComment.builder()
                .content("댓글")
                .member(writer)
                .accompany(accompany)
                .build();
        // when, then
        assertThatThrownBy(() -> comment.validateIsCommentWriter(otherMember.getId()))
                .isInstanceOf(BaseException.class);
    }
}
