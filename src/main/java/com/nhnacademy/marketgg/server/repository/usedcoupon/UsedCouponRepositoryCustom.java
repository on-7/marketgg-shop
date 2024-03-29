package com.nhnacademy.marketgg.server.repository.usedcoupon;

import com.nhnacademy.marketgg.server.dto.response.coupon.UsedCouponResponse;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UsedCouponRepositoryCustom {

    /**
     * 쿠폰 번호가 사용 쿠폰에 등록되었는가를 확인하는 메소드입니다.
     *
     * @param couponId - 확인할 쿠폰의 식별번호입니다.
     * @return 등록되어 있다면 true(사용불가), 등록되어 있지 않다면 false(사용가능)를 반환합니다.
     * @since 1.0.0
     */
    boolean existsCouponId(final Long couponId, final Long memberId);

    /**
     * 특정 주문에 사용한 쿠폰 번호를 조회하는 메소드입니다.
     *
     * @param orderId - 쿠폰 번호를 조회할 주문의 식별번호입니다.
     * @return 사용한 쿠폰의 식별번호를 반환합니다.
     * @since 1.0.0
     */
    Optional<Long> findByOrderId(final Long orderId);

    /**
     * 특정 주문에 사용한 쿠폰 이름을 조회하는 메소드입니다.
     *
     * @param orderId - 쿠폰 이름을 조회할 주문의 식별번호입니다.
     * @return 사용한 쿠폰의 이름을 반환합니다.
     */
    UsedCouponResponse findUsedCouponName(Long orderId);

}
