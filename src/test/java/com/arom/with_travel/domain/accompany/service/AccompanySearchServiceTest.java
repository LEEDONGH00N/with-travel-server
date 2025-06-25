package com.arom.with_travel.domain.accompany.service;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyBriefResponse;
import com.arom.with_travel.domain.accompanies.model.Accompany;
import com.arom.with_travel.domain.accompanies.model.City;
import com.arom.with_travel.domain.accompanies.model.Continent;
import com.arom.with_travel.domain.accompanies.model.Country;
import com.arom.with_travel.domain.accompanies.repository.accompany.AccompanyRepository;
import com.arom.with_travel.domain.accompanies.service.AccompanySearchService;
import com.arom.with_travel.domain.accompany.AccompanyFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
public class AccompanySearchServiceTest {

    @Mock
    private AccompanyRepository accompanyRepository;

    @InjectMocks
    private AccompanySearchService accompanySearchService;

    private Accompany accompany;

    @BeforeEach
    void setUp() {
        accompany = AccompanyFixture.동행_생성();
        accompany.post(AccompanyFixture.동행_작성자_생성());
        setField(accompany, "id", 1L);
    }

    @Test
    void 대륙별_동행_목록_조회_성공() {
        // given
        Continent continent = Continent.ASIA;
        Pageable pageable = PageRequest.of(0, 10);
        List<Accompany> accompanies = List.of(accompany);
        Page<Accompany> accompanyPage = new PageImpl<>(
                accompanies,
                pageable,
                accompanies.size()
        );
        given(accompanyRepository.findByContinent(any(), any()))
                .willReturn(accompanyPage);

        // when
        List<AccompanyBriefResponse> result = accompanySearchService.searchByContinent(continent, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        verify(accompanyRepository, times(1)).findByContinent(eq(continent), eq(pageable));
    }

    @Test
    void 필터_동행_목록_조회_성공() {
        // given
        Continent continent = Continent.ASIA;
        Country country = Country.JAPAN;
        City city = City.TOKYO;
        LocalDate startDate = LocalDate.now();
        Long lastId = 1L;
        int size = 10;

        Slice<Accompany> accompanySlice = new SliceImpl<>(
                List.of(accompany),
                PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt")),
                false
        );

        given(accompanyRepository.findByFiltersWithNoOffset(any(), any(), any(), any(), any(), any())).willReturn(accompanySlice);

        // when
        Slice<AccompanyBriefResponse> result = accompanySearchService.getFilteredAccompanies(
                continent, country, city, startDate, lastId, size
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(accompanyRepository, times(1))
                .findByFiltersWithNoOffset(eq(continent), eq(country), eq(city), eq(startDate), eq(lastId), any(Pageable.class));
    }
}
