package com.arom.with_travel.domain.accompanies.controller;

import com.arom.with_travel.domain.accompanies.dto.request.AccompanyNewCommentRequest;
import com.arom.with_travel.domain.accompanies.dto.request.AccompanyUpdateCommentRequest;
import com.arom.with_travel.domain.accompanies.dto.response.AccompanyCommentSliceResponse;
import com.arom.with_travel.domain.accompanies.service.AccompanyCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accompanies/{accompanyId}/comments")
public class AccompanyCommentController {

    private final AccompanyCommentService accompanyCommentService;

    @PostMapping
    public void createComments(@PathVariable Long accompanyId,
                               @RequestBody AccompanyNewCommentRequest request){
        accompanyCommentService.addCommentToAccompany(accompanyId, 1L, request);
    }

    @GetMapping
    public AccompanyCommentSliceResponse getComments(
            @PathVariable Long accompanyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size) {
        return accompanyCommentService.getComments(accompanyId, lastCreatedAt, lastId, size);
    }

    @PatchMapping
    public void modifyComment(@PathVariable Long accompanyId,
                              @RequestBody AccompanyUpdateCommentRequest request){
        accompanyCommentService.updateComment(accompanyId, 1L ,request);
    }

    @DeleteMapping
    public void removeComment(@PathVariable Long accompanyId){
        accompanyCommentService.deleteComment(accompanyId, 1L);
    }
}
