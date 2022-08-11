package com.nhnacademy.marketgg.server.dto.response.customerservice;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PostResponseForReady {

    private final Long id;

    private final String categoryCode;

    private final String title;

    private final String content;

    private final String reason;

    private final String status;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private final List<CommentReady> commentReady;

}