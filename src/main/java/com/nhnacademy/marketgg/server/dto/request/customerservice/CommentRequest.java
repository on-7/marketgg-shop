package com.nhnacademy.marketgg.server.dto.request.customerservice;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 1:1 문의의 댓글을 달 때 사용되는 DTO 입니다.
 *
 * @author 박세완
 * @version 1.0.0
 */
@NoArgsConstructor
@Getter
public class CommentRequest {

    @Schema(name = "댓글 내용", description = "1:1문의의 댓글 내용입니다.", example = "싫은데요")
    @NotBlank
    @Size(min = 1, max = 255)
    private String content;

}
