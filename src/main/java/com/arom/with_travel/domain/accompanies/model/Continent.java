package com.arom.with_travel.domain.accompanies.model;

import com.arom.with_travel.global.exception.BaseException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static com.arom.with_travel.global.exception.error.ErrorCode.ACCOMPANY_POST_ERROR;

@Getter
@AllArgsConstructor
public enum Continent {
    ASIA("아시아"),
    AFRICA("아프리카"),
    NORTH_AMERICA("북아메리카"),
    SOUTH_AMERICA("남아메리카"),
    ANTARCTICA("남극"),
    EUROPE("유럽"),
    OCEANIA("오세아니아");
    ;

    @JsonValue private final String name;

    @JsonCreator
    public static Continent fromContinent(String val) {
        return Arrays.stream(values())
                .filter(type -> type.getName().equals(val))
                .findAny()
                .orElseThrow(() -> BaseException.from(ACCOMPANY_POST_ERROR));
    }
}
