package com.nhnacademy.marketgg.server.dto.response.order;

import com.nhnacademy.marketgg.server.dto.response.orderproduct.OrderProductResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class OrderDetailRetrieveResponse {

    private final Long id;

    private final Long memberId;

    private final Long totalAmount;

    private final String orderStatus;

    private final Integer usedPoint;

    private final Integer trackingNo;

    private final Integer zipCode;

    private final String address;

    private final String detailAddress;

    private final LocalDateTime createdAt;

    private List<OrderProductResponse> orderProductList;

    public void addOrderDetail(List<OrderProductResponse> orderProductList) {
        this.orderProductList = orderProductList;
    }

}
