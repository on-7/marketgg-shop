package com.nhnacademy.marketgg.server.dto.response.image;

import com.nhnacademy.marketgg.server.entity.Asset;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ImageResponse {

    private final String name;

    private final Long length;

    private final String imageAddress;

    private final Integer imageSequence;

    private final Asset asset;

}
