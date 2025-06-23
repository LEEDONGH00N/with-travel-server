package com.arom.with_travel.domain.accompanies.dto.event;

import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.member.Member;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AccompanyAppliedEvent {

    private final Long accompanyId;
    private final Long ownerId;
    private final Long proposerId;
    private final String proposerNickName;
    private final LocalDateTime occurredAt = LocalDateTime.now();

    public AccompanyAppliedEvent(Accompany accompany, Member proposer) {
        this.accompanyId = accompany.getId();
        this.ownerId = accompany.getOwnerId();
        this.proposerId = proposer.getId();
        this.proposerNickName = proposer.getNickname();
    }
}
