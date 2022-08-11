package com.nhnacademy.marketgg.server.service.order;

import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.order.OrderCreateRequest;
import com.nhnacademy.marketgg.server.dto.response.order.OrderCreateResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderDetailResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderResponse;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.Order;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.repository.deliveryaddress.DeliveryAddressRepository;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.repository.order.OrderRepository;
import com.nhnacademy.marketgg.server.repository.orderproduct.OrderProductRepository;
import com.nhnacademy.marketgg.server.repository.pointhistory.PointHistoryRepository;
import com.nhnacademy.marketgg.server.repository.usedcoupon.UsedCouponRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OrderProductRepository orderProductRepository;
    private final DeliveryAddressRepository deliveryRepository;
    private final PointHistoryRepository pointRepository;
    private final UsedCouponRepository usedCouponRepository;

    @Transactional(readOnly = true)
    @Override
    public OrderCreateResponse createOrder(final OrderCreateRequest orderRequest, final Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Order order = new Order(member, orderRequest);

        orderRepository.save(order);
        // Memo: 주문, 사용쿠폰, 포인트이력, 주문배송지, 주문상품 save(추가 or 수정 or 삭제)


        return new OrderCreateResponse(orderRequest.getTotalAmount(), orderRequest.getOrderType(),
            orderRequest.isDirectBuy());
    }

    // 주문 목록 조회 - 관리자(전체), 회원(본인)
    @Override
    public List<OrderResponse> retrieveOrderList(final MemberInfo memberinfo) {
        // return (isUser ? orderRepository.findOrderListById(memberId) : orderRepository.findAllOrder());
        return orderRepository.findOrderList(memberinfo.getId(), memberinfo.isUser());
    }

    @Override
    public OrderDetailResponse retrieveOrderDetail(final Long orderId, final MemberInfo memberInfo) {
        return orderRepository.findOrderDetail(orderId, memberInfo.getId(), memberInfo.isUser());
    }

    @Transactional(readOnly = true)
    @Override
    public void deleteOrder(final Long orderId) {
        orderRepository.deleteById(orderId);
    }
}