package com.arom.with_travel.domain.accompany.repository;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyPostRequest;
import com.arom.with_travel.domain.accompanies.model.*;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
public class AccompanyRepositoryTest {

    @Autowired
    private AccompanyRepository accompanyRepository;

    private Accompany accompany;
    private AccompanyPostRequest request;

    @BeforeEach
    void setUp() {
        request = new AccompanyPostRequest();
        accompanyPostRequestSetUp();
        accompany = Accompany.from(request);
        accompanyRepository.save(accompany);
    }

    @Test
    void 대륙으로_동행찾기_성공(){
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Accompany> result = accompanyRepository.findByContinent(Continent.ASIA, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    private void accompanyPostRequestSetUp() {
        setField(request, "continent", Continent.ASIA);
        setField(request, "country", Country.JAPAN);
        setField(request, "city", City.TOKYO);
        setField(request, "accompanyType", AccompanyType.EVENT);
        setField(request, "destination", "도쿄");
        setField(request, "startDate", LocalDate.of(2025, 5, 1));
        setField(request, "startTime", LocalTime.of(10, 0));
        setField(request, "endDate", LocalDate.of(2025, 5, 2));
        setField(request, "endTime", LocalTime.of(11, 0));
        setField(request, "title", "도쿄 여행 메이트 모집");
        setField(request, "description", "10일간 도쿄 여행하실 분 구합니다.");
        setField(request, "maxParticipants", 3);
    }
}
