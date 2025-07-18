package com.arom.with_travel.domain.member.dto;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.image.Image;
import com.arom.with_travel.domain.member.Member;

import java.util.List;

public record MemberProfileResponseDto(
        boolean isPrinciple,
        String memberNickname,
        Member.TravelType travelType,
        Image image,
        String introduction,
        List<Accompany>[] accompanies
        //동행 데이터 추가
){
     public static MemberProfileResponseDto from(boolean isPrinciple,Member member, List<Accompany>[] accompanies){
        return new MemberProfileResponseDto(
                isPrinciple,
                member.getNickname(),
                member.getTravelType(),
                member.getImage(),
                member.getIntroduction(),
                accompanies
        );
    }
}
