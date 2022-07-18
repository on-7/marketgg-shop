package com.nhnacademy.marketgg.server.controller;

import com.nhnacademy.marketgg.server.dto.response.PointRetrieveResponse;
import com.nhnacademy.marketgg.server.service.PointService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop/v1/members/{memberId}/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    private static final String DEFAULT_MEMBER = "/shop/v1/members";

    @GetMapping
    public ResponseEntity<List<PointRetrieveResponse>> retrievePointHistory(@PathVariable final Long memberId) {
        List<PointRetrieveResponse> responses = pointService.retrievePointHistories(memberId);

        return ResponseEntity.status(HttpStatus.OK)
                .location(URI.create(DEFAULT_MEMBER + "/" + memberId + "/points"))
                .body(responses);
    }


}