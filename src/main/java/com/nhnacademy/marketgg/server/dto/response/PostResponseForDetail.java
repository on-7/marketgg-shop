package com.nhnacademy.marketgg.server.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class PostResponseForDetail {

    private final Long id;

    private final String title;

    private final String content;

    private final String reason;

    private final String status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

}