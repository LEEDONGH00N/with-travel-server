package com.arom.with_travel.domain.member.service;

import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.domain.member.model.MemberTest;
import com.arom.with_travel.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SuppressWarnings("NonAsciiCharacters")
public class MemberSaveServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {

    }

    @Test
    void 회원_저장하기(){
        Member member = MemberTest.회원_생성_1();
        memberRepository.save(member);
    }

    @Test
    void 회원_불러오기_이메일(){
        //given
        Member member = MemberTest.회원_생성_1();
        memberRepository.save(member);

        //when
        Member result = memberRepository.findByEmail(member.getEmail()).orElse(null);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
    }
}
