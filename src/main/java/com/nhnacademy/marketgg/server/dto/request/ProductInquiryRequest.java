package com.nhnacademy.marketgg.server.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
public class ProductInquiryRequest {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Length(max = 50, message = "제목의 최대 글자 수는 50자입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    @Length(max = 200, message = "내용의 최대 글자 수는 200자입니다.")
    private String content;

    @NotNull
    private Boolean isSecret;

    private String adminReply;

}
