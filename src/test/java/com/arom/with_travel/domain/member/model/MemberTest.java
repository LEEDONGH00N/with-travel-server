package com.arom.with_travel.domain.member.model;

import com.arom.with_travel.domain.member.Member;

import java.time.LocalDate;

public class MemberTest {
    public static Member 회원_생성_1(){
            return Member.builder()
                    .id(21L)
                    .oauthId("oauthId_1")
                    .email("test1@test.com")
                    .birth(LocalDate.of(2000, 11, 30))
                    .gender(Member.Gender.MALE)
                    .phone("010-1234-5678")
                    .name("test")
                    .loginType(Member.LoginType.KAKAO)
                    .nickname("나요")
                    .introduction("안녕하세요")
                    .travelType(Member.TravelType.USER)
                    .role(Member.Role.USER)
                    .build();
    }

    public static Member 회원_생성_2(){
        return Member.builder()
                .id(20L)
                .oauthId("oauthId_1")
                .email("test2@test.com")
                .birth(LocalDate.of(1950, 6, 4))
                .gender(Member.Gender.FEMALE)
                .phone("010-1413-1523")
                .name("테스트")
                .loginType(Member.LoginType.KAKAO)
                .nickname("goodTravel")
                .introduction("hello nice to meet you")
                .travelType(Member.TravelType.USER)
                .role(Member.Role.USER)
                .build();
    }

    public static Member 회원_생성_3(){
        return Member.builder()
                .id(22L)
                .oauthId("oauthId_1")
                .email("test3@test.com")
                .birth(LocalDate.of(2002, 7, 14))
                .gender(Member.Gender.MALE)
                .phone("010-1567-9125")
                .name("테스트용")
                .loginType(Member.LoginType.KAKAO)
                .nickname("용용")
                .introduction("hello nice to meet you")
                .travelType(Member.TravelType.USER)
                .role(Member.Role.USER)
                .build();
    }

}
