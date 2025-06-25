package com.arom.with_travel.domain.accompany.repository;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.AccompanyComment;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyCommentRepository;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
class AccompanyCommentRepositoryTest {
    @Autowired
    private AccompanyCommentRepository accompanyCommentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AccompanyRepository accompanyRepository;

    private Member writer;
    private Accompany accompany;

    @BeforeEach
    void setUp() {
        writer = memberRepository.save(Member.create("writer", "writer@test.com", Member.Role.USER));

        accompany = AccompanyFixture.동행_생성();
        accompany.post(writer);
        accompany = accompanyRepository.save(accompany);

        for (int i = 1; i <= 5; i++) {
            AccompanyComment comment = AccompanyComment.builder()
                    .content("댓글 " + i)
                    .member(writer)
                    .accompany(accompany)
                    .build();
            accompanyCommentRepository.save(comment);
        }
    }

    @Test
    void 커서_페이지네이션_댓글_조회() {
        // given
        Long accompanyId = accompany.getId();
        LocalDateTime lastCreatedAt = null;
        Long lastId = null;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(0, pageSize + 1);

        // when
        Slice<AccompanyComment> result = accompanyCommentRepository.findCommentsWithCursor(
                accompanyId, lastCreatedAt, lastId, pageable
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent().size()).isLessThanOrEqualTo(pageSize + 1);
        assertThat(result.getContent())
                .extracting(AccompanyComment::getContent)
                .contains("댓글 1", "댓글 2", "댓글 3");
    }
}