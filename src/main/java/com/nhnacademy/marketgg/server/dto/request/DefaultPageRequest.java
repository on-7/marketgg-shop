package com.nhnacademy.marketgg.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@AllArgsConstructor
@Getter
public class DefaultPageRequest {

    private int page;
    private int size;

    public DefaultPageRequest(Integer page) {
        this.page = page;
        this.size = 9;
    }

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

}
