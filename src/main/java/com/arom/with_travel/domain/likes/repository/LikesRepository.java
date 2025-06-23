package com.arom.with_travel.domain.likes.repository;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.likes.Likes;
import com.arom.with_travel.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    // TODO : 연관관계 엔티티가 아닌 필드로 조회하도록 변경 예정
    Optional<Likes> findByAccompanyAndMember(Accompany accompany, Member member);

    @Query("SELECT COUNT(l) FROM Likes l WHERE l.accompany.id = :accompanyId")
    long countByAccompanyId(@Param("accompanyId") Long accompanyId);
}
