package com.arom.with_travel.domain.accompanies.dto.response;

import com.arom.with_travel.domain.accompanies.model.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class AccompanyDetailsResponse {
    private Continent continent;
    private Country country;
    private City city;
    private String destination;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate endDate;
    private String title;
    private String description;
    private int recruitmentCount;
    private AccompanyType accompanyType;
    private String writer;
    private Long views;
    private int likes;

    public static AccompanyDetailsResponse from(Accompany accompany){
        return AccompanyDetailsResponse.builder()
                .continent(accompany.getContinent())
                .country(accompany.getCountry())
                .city(accompany.getCity())
                .accompanyType(accompany.getAccompanyType())
                .destination(accompany.getDestination())
                .startDate(accompany.getStartDate())
                .startTime(accompany.getStartTime())
                .endDate(accompany.getEndDate())
                .title(accompany.getTitle())
                .description(accompany.getDescription())
                .recruitmentCount(accompany.getRecruitmentCount())
                .writer(accompany.getMember().getNickname())
                .views(accompany.getViews())
                .likes(accompany.showLikes())
                .build();
    }
}
