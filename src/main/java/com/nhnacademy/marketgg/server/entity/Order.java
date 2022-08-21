package com.nhnacademy.marketgg.server.entity;

import com.nhnacademy.marketgg.server.constant.OrderStatus;
import com.nhnacademy.marketgg.server.constant.PaymentType;
import com.nhnacademy.marketgg.server.dto.request.order.OrderCreateRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 주문 개체입니다.
 *
 * @author 공통
 * @author 김정민
 * @version 1.0
 * @since 1.0
 */
@Table(name = "orders")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_no")
    @NotNull
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_no")
    @NotNull
    private Member member;

    @Column(name = "total_amount")
    @NotNull
    private Long totalAmount;

    @Column(name = "order_status")
    @NotBlank
    @Size(min = 1, max = 10)
    private String orderStatus;

    @Column(name = "used_point")
    @NotNull
    private Integer usedPoint;

    @Column(name = "tracking_no")
    private String trackingNo;

    @Column(name = "zip_code")
    @NotNull
    private Integer zipCode;

    @Column(name = "address")
    @NotNull
    private String address;

    @Column(name = "detail_address")
    @NotNull
    private String detailAddress;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @NotNull
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 주문 생성자입니다.
     *
     * @param member       - 주문을 한 회원 객체
     * @param orderRequest - 주문 요청 객체
     */
    public Order(final Member member, final DeliveryAddress deliveryAddress, final OrderCreateRequest orderRequest) {
        this.member = member;
        this.totalAmount = orderRequest.getTotalAmount();
        this.orderStatus = orderRequest.getPaymentType().equals(PaymentType.VIRTUAL.getType())
                ? OrderStatus.DEPOSIT_WAITING.getStatus() : OrderStatus.PAY_WAITING.getStatus();
        this.usedPoint = orderRequest.getUsedPoint();
        this.zipCode = deliveryAddress.getZipCode();
        this.address = deliveryAddress.getAddress();
        this.detailAddress = deliveryAddress.getDetailAddress();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public static Order test() {
        return new Order();
    }

    public void updateStatus(String status) {
        this.orderStatus = status;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void insertTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }
}
