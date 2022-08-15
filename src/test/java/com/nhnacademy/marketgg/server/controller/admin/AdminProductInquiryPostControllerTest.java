package com.nhnacademy.marketgg.server.controller.admin;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.server.aop.AspectUtils;
import com.nhnacademy.marketgg.server.aop.RoleCheckAspect;
import com.nhnacademy.marketgg.server.dto.request.product.ProductInquiryReplyRequest;
import com.nhnacademy.marketgg.server.service.product.ProductInquiryPostService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminProductInquiryPostController.class)
@Import({
    RoleCheckAspect.class
})
class AdminProductInquiryPostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProductInquiryPostService productInquiryPostService;

    HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(AspectUtils.AUTH_ID, UUID.randomUUID().toString());
        httpHeaders.add(AspectUtils.WWW_AUTHENTICATE, "[\"ROLE_ADMIN\"]");
    }

    @Test
    @DisplayName("상품 문의 답글 등록 테스트")
    void testUpdateProductInquiryReply() throws Exception {
        ProductInquiryReplyRequest replyRequest = new ProductInquiryReplyRequest();
        ReflectionTestUtils.setField(replyRequest, "productId", 1L);
        ReflectionTestUtils.setField(replyRequest, "inquiryId", 1L);
        ReflectionTestUtils.setField(replyRequest, "adminReply", "고객님 안녕하세요 재입고 예정은 없습니다.");
        String content = objectMapper.writeValueAsString(replyRequest);

        doNothing().when(productInquiryPostService)
                   .updateProductInquiryReply(anyString(), anyLong(), anyLong());

        this.mockMvc.perform(put("/admin/products/inquiry-reply")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                    .andExpect(status().isOk());
        verify(productInquiryPostService, times(1))
            .updateProductInquiryReply(anyString(), anyLong(), anyLong());
    }
}