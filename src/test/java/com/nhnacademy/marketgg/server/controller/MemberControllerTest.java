package com.nhnacademy.marketgg.server.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.server.aop.AspectUtils;
import com.nhnacademy.marketgg.server.controller.member.MemberController;
import com.nhnacademy.marketgg.server.dto.PageEntity;
import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.coupon.GivenCouponCreateRequest;
import com.nhnacademy.marketgg.server.dto.response.coupon.GivenCouponResponse;
import com.nhnacademy.marketgg.server.dto.response.member.MemberResponse;
import com.nhnacademy.marketgg.server.dto.response.product.ProductInquiryResponse;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.service.coupon.GivenCouponService;
import com.nhnacademy.marketgg.server.service.member.MemberService;
import com.nhnacademy.marketgg.server.service.point.PointService;
import com.nhnacademy.marketgg.server.service.product.ProductInquiryPostService;
import com.nhnacademy.marketgg.server.service.product.ReviewService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @MockBean
    PointService pointService;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    GivenCouponService givenCouponService;

    @MockBean
    ProductInquiryPostService inquiryPostService;

    @MockBean
    ReviewService reviewService;

    HttpHeaders httpHeaders;

    GivenCouponCreateRequest givenCouponCreateRequest;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
        httpHeaders.add(AspectUtils.AUTH_ID, UUID.randomUUID().toString());
        httpHeaders.add(AspectUtils.WWW_AUTHENTICATE, "[\"ROLE_ADMIN\"]");

        givenCouponCreateRequest = new GivenCouponCreateRequest();
        ReflectionTestUtils.setField(givenCouponCreateRequest, "name", "신규 회원 쿠폰");
    }

    @Test
    @DisplayName("사용자 조회")
    void testRetrieveMember() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        MemberResponse memberResponse = MemberResponse.builder()
                                                      .memberGrade(null)
                                                      .birthDay(now.toLocalDate())
                                                      .gender('M')
                                                      .build();

        this.mockMvc.perform(get("/members")
                                 .headers(httpHeaders)
                                 .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success", equalTo(true)))
                    .andDo(print());
    }

    @Test
    @DisplayName("회원에게 지급 쿠폰 생성")
    void testCreateGivenCoupons() throws Exception {
        willDoNothing().given(givenCouponService)
                       .createGivenCoupons(any(MemberInfo.class), any(GivenCouponCreateRequest.class));
        String content = objectMapper.writeValueAsString(givenCouponCreateRequest);

        this.mockMvc.perform(post("/members/coupons")
                                 .headers(httpHeaders)
                                 .contentType(MediaType.APPLICATION_JSON)
                                 .content(content))
                    .andExpect(status().isCreated());

        then(givenCouponService).should(times(1)).createGivenCoupons(any(MemberInfo.class),
                                                                     any(GivenCouponCreateRequest.class));
    }

    @Test
    @DisplayName("회원에게 지급된 쿠폰 전체 조회")
    void testRetrieveGivenCoupons() throws Exception {
        PageEntity<GivenCouponResponse> pageEntity = new PageEntity<>(1, 1, 1, List.of());
        given(givenCouponService.retrieveGivenCoupons(any(MemberInfo.class), any(Pageable.class))).willReturn(
            pageEntity);

        this.mockMvc.perform(get("/members/coupons")
                                 .headers(httpHeaders))
                    .andExpect(status().isOk());

        then(givenCouponService).should(times(1)).retrieveGivenCoupons(any(MemberInfo.class), any(Pageable.class));
    }

    @Test
    @DisplayName("회원이 작성한 전체 상품 문의 조회 테스트")
    void testRetrieveProductInquiryByMemberId() throws Exception {
        PageEntity<ProductInquiryResponse> pageEntity = new PageEntity<>(1, 1, 1, List.of());

        given(inquiryPostService.retrieveProductInquiryByMemberId(any(MemberInfo.class), any(PageRequest.class)))
            .willReturn(pageEntity);

        this.mockMvc.perform(get("/members/product-inquiries")
                                 .headers(httpHeaders)
                                 .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
    }

}
