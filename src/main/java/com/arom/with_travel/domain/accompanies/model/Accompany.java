package com.arom.with_travel.domain.accompanies.model;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyPostRequest;
import com.arom.with_travel.domain.image.Image;
import com.arom.with_travel.domain.likes.Likes;
import com.arom.with_travel.domain.member.Member;
import com.arom.with_travel.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@Table(name = "accompanies",
//        indexes = {
//                @Index(name = "idx_accompanies_country", columnList = "country"),
//                @Index(name = "idx_accompanies_created_at", columnList = "created_at")
//        })
@SQLDelete(sql = "UPDATE member SET is_deleted = true, deleted_at = now() where id = ?")
@SQLRestriction("is_deleted is FALSE")
public class    Accompany extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int maxParticipants;

    @NotNull
    private String destination;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccompanyType accompanyType;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private Long views = 0L;

    @Column
    @Enumerated(EnumType.STRING)
    private Continent continent;

    @Column
    @Enumerated(EnumType.STRING)
    private Country country;

    @Column
    @Enumerated(EnumType.STRING)
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "accompany")
    private List<AccompanyReviews> accompanyReviews = new ArrayList<>();

    @OneToMany(mappedBy = "accompany")
    private List<AccompanyApply> accompanyApplies = new ArrayList<>();

    @OneToMany(mappedBy = "accompany")
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "accompany")
    private List<AccompanyComment> accompanyComments = new ArrayList<>();

    @OneToMany(mappedBy = "accompany")
    private List<Likes> likes = new ArrayList<>();

    @Builder
    public Accompany(LocalTime startTime,
                     LocalDate startDate,
                     LocalDate endDate,
                     LocalTime endTime,
                     String accompanyDescription,
                     String accompanyTitle,
                     AccompanyType accompanyType,
                     String destination,
                     int maxParticipants,
                     Continent continent,
                     Country country,
                     City city) {
        this.startTime = startTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.endTime = endTime;
        this.title = accompanyTitle;
        this.description= accompanyDescription;
        this.accompanyType = accompanyType;
        this.destination = destination;
        this.maxParticipants = maxParticipants;
        this.continent = continent;
        this.country = country;
        this.city = city;
    }

    public void post(Member member){
        this.member = member;
        member.getAccompanies().add(this);
    }

    public void addView(){
        views++;
    }

    public Long getOwnerId(){
        return member.getId();
    }

    public static Accompany from(AccompanyPostRequest request){
        return Accompany.builder()
                .startTime(request.getStartTime())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .endTime(request.getEndTime())
                .accompanyTitle(request.getTitle())
                .accompanyDescription(request.getDescription())
                .maxParticipants(request.getMaxParticipants())
                .destination(request.getDestination())
                .accompanyType(request.getAccompanyType())
                .continent(request.getContinent())
                .country(request.getCountry())
                .city(request.getCity())
                .build();
    }
}
