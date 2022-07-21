package com.nhnacademy.marketgg.server.entity;

import com.nhnacademy.marketgg.server.dto.request.GivenCouponRequest;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Table(name = "given_coupons")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GivenCoupon {

    @EmbeddedId
    private Pk pk;

    @MapsId(value = "couponNo")
    @ManyToOne
    @JoinColumn(name = "coupon_no")
    private Coupon coupon;

    @MapsId(value = "memberNo")
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @EqualsAndHashCode
    public static class Pk implements Serializable {

        @Column(name = "coupon_no")
        private Long couponNo;

        @Column(name = "member_no")
        private Long memberNo;

        public Pk(Long couponNo, Long memberNo) {
            this.couponNo = couponNo;
            this.memberNo = memberNo;
        }

    }

    public GivenCoupon(final Coupon coupon, final Member member, final GivenCouponRequest givenCouponRequest) {
        this.pk = new Pk(givenCouponRequest.getCouponNo(), member.getId());
        this.coupon = coupon;
        this.member = member;
        this.createdAt = LocalDateTime.now();
    }

}
