package com.nhnacademy.marketgg.server.service.impl;

import com.nhnacademy.marketgg.server.dto.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.OrderCreateRequest;
import com.nhnacademy.marketgg.server.dto.response.OrderDetailResponse;
import com.nhnacademy.marketgg.server.dto.response.OrderResponse;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.Order;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.repository.order.OrderRepository;
import com.nhnacademy.marketgg.server.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void createOrder(final OrderCreateRequest orderRequest, final Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Order order = new Order(member, orderRequest);

        orderRepository.save(order);
        // Memo: 주문, 사용쿠폰, 포인트이력, 주문배송지, 주문상품, 장바구니 save(추가, 수정, 삭제)
    }

    // 주문 목록 조회 - 관리자(전체), 회원(본인)
    @Override
    public List<OrderResponse> retrieveOrderList(final MemberInfo memberinfo) {
        // return (isUser ? orderRepository.findOrderListById(memberId) : orderRepository.findAllOrder());
        return orderRepository.findOrderList(memberinfo.getId(), memberinfo.isUser());
    }

    @Override
    public OrderDetailResponse retrieveOrder(Long orderId, MemberInfo memberInfo) {
        return orderRepository.findOrderDetail(orderId, memberInfo.getId(), memberInfo.isUser());
    }

}
