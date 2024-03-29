package com.nhnacademy.marketgg.server.eventlistener.event.givencoupon;


import com.nhnacademy.marketgg.server.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 쿠폰 지급에 필요한 Data 를 가진 이벤트 클래스 입니다.
 *
 * @author 민아영
 * @version 1.0.0
 * @since 1.0.0
 */
@AllArgsConstructor
@Getter
public class GiveCouponEvent {

    private String couponName;

    private Member member;

}
