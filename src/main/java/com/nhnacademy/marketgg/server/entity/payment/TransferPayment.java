package com.nhnacademy.marketgg.server.entity.payment;

import com.nhnacademy.marketgg.server.constant.payment.BankCode;
import com.nhnacademy.marketgg.server.constant.payment.SettlementStatus;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 계좌이체로 결제했을 때 이체 정보가 담기는 개체입니다.
 *
 * @author 김정민
 * @author 김훈민
 * @author 민아영
 * @author 박세완
 * @author 윤동열
 * @author 이제훈
 * @author 조현진
 * @version 1.0
 * @since 1.0
 */
@Table(name = "transfer_payments")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TransferPayment {

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @Getter
    @EqualsAndHashCode
    public static class Pk implements Serializable {

        @Column(name = "payment_no")
        @NotNull
        private Long paymentId;

    }

    @Id
    private Pk pk;

    @MapsId(value = "paymentId")
    @OneToOne
    @JoinColumn(name = "payment_no")
    private Payment payment;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull
    private BankCode bank;

    @Column(name = "settlement_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private SettlementStatus settlementStatus;

}
