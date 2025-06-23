package com.arom.with_travel.domain.accompanies.dto.response;

import com.arom.with_travel.domain.accompanies.model.AccompanyComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@AllArgsConstructor
public class AccompanyCommentSliceResponse {
    private List<AccompanyCommentResponse> comments;
    private boolean hasNext;

    public static AccompanyCommentSliceResponse from(Slice<AccompanyComment> slice) {
        return new AccompanyCommentSliceResponse(
                AccompanyCommentResponse.fromEntities(slice.getContent()),
                slice.hasNext()
        );
    }
}