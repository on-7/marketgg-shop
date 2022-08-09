package com.nhnacademy.marketgg.server.entity;

import com.nhnacademy.marketgg.server.dto.request.deliveryaddress.DeliveryAddressCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.deliveryaddress.DeliveryAddressUpdateRequest;
import com.nhnacademy.marketgg.server.dto.request.member.ShopMemberSignUpRequest;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 배송 주소 개체입니다.
 *
 * @author 공통
 * @version 1.0.0
 */
@Table(name = "delivery_addresses")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DeliveryAddress {

    @EmbeddedId
    private Pk pk;

    @MapsId("memberNo")
    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;

    @Column(name = "is_default_address")
    private Boolean isDefaultAddress;

    @Column(name = "zip_code")
    private Integer zipCode;

    @Column
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class Pk implements Serializable {

        @Column(name = "delivery_address_no")
        private Long id;

        @Column(name = "member_no")
        private Long memberNo;

        public Pk(Long memberNo) {
            this.memberNo = memberNo;
        }


    }
    public DeliveryAddress(final Member signUpMember,
                           final ShopMemberSignUpRequest shopMemberSignUpRequest) {

        this.pk = new Pk(signUpMember.getId());
        this.member = signUpMember;
        this.isDefaultAddress = Boolean.TRUE;
        this.zipCode = shopMemberSignUpRequest.getZipcode();
        this.address = shopMemberSignUpRequest.getAddress();
        this.detailAddress = shopMemberSignUpRequest.getDetailAddress();

    }

    public DeliveryAddress(final Pk pk,
                           final Member member,
                           final DeliveryAddressCreateRequest createRequest) {

        this.pk = pk;
        this.member = member;
        this.isDefaultAddress = createRequest.isDefaultAddress();
        this.zipCode = createRequest.getZipCode();
        this.address = createRequest.getAddress();
        this.detailAddress = createRequest.getDetailAddress();

    }

    public void update(final Member member,
                       final DeliveryAddressUpdateRequest updateRequest) {

        this.member = member;
        this.isDefaultAddress = updateRequest.isDefaultAddress();
        this.zipCode = updateRequest.getZipCode();
        this.address = updateRequest.getAddress();
        this.detailAddress = updateRequest.getDetailAddress();

    }

}
