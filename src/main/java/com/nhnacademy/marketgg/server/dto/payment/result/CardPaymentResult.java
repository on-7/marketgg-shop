package com.nhnacademy.marketgg.server.dto.payment.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카드 결제 시 제공되는 카드 관련 정보입니다.
 *
 * @author 이제훈
 * @version 1.0
 * @since 1.0
 */
@NoArgsConstructor
@Getter
public class CardPaymentResult {

    @NotNull
    private Long amount;

    @JsonProperty("company")
    @NotBlank
    @Size(min = 2, max = 10)
    private String companyCode;

    @NotBlank
    @Size(min = 2, max = 10)
    private String number;

    @NotNull
    private Integer installmentPlanMonths;

    @NotBlank
    @Size(min = 2, max = 10)
    private String cardType;

    @NotBlank
    @Size(min = 2, max = 10)
    private String ownerType;

    @NotBlank
    @Size(min = 2, max = 255)
    private String receiptUrl;

    @NotBlank
    @Size(min = 2, max = 20)
    private String acquireStatus;

}
