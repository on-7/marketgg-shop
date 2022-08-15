package com.nhnacademy.marketgg.server.dto.request.product;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProductInquiryReplyRequest {

    @NotNull
    private Long productId;

    @NotNull
    private Long inquiryId;

    @NotBlank
    @Size(min = 10, max = 300, message = "문의 답변 내용은 10자 이상, 300자 이하만 가능합니다.")
    private String adminReply;

}