package com.arom.with_travel.domain.accompanies.repository.accompany;

import com.arom.with_travel.domain.accompanies.model.AccompanyApply;
import com.arom.with_travel.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccompanyApplyRepository extends JpaRepository<AccompanyApply, Long> {
    List<AccompanyApply> findAccompanyAppliesByMember(Member member);
}
