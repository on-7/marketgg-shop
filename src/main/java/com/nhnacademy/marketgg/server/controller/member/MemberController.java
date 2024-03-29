package com.nhnacademy.marketgg.server.controller.member;

import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.server.annotation.Auth;
import com.nhnacademy.marketgg.server.dto.PageEntity;
import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.info.AuthInfo;
import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.DefaultPageRequest;
import com.nhnacademy.marketgg.server.dto.request.coupon.GivenCouponCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.member.MemberUpdateRequest;
import com.nhnacademy.marketgg.server.dto.request.member.MemberWithdrawRequest;
import com.nhnacademy.marketgg.server.dto.request.member.SignupRequest;
import com.nhnacademy.marketgg.server.dto.response.auth.UuidTokenResponse;
import com.nhnacademy.marketgg.server.dto.response.coupon.GivenCouponResponse;
import com.nhnacademy.marketgg.server.dto.response.member.MemberResponse;
import com.nhnacademy.marketgg.server.dto.response.product.ProductInquiryResponse;
import com.nhnacademy.marketgg.server.dto.response.review.ReviewResponse;
import com.nhnacademy.marketgg.server.service.coupon.GivenCouponService;
import com.nhnacademy.marketgg.server.service.member.MemberService;
import com.nhnacademy.marketgg.server.service.product.ProductInquiryPostService;
import com.nhnacademy.marketgg.server.service.product.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 회원관리에 관련된 RestController 입니다.
 *
 * @author 김훈민, 민아영, 박세완, 조현진
 * @version 1.0.0
 */
@Slf4j
@Auth
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final GivenCouponService givenCouponService;
    private final ProductInquiryPostService productInquiryPostService;
    private final ReviewService reviewService;

    public static final String JWT_EXPIRE = "JWT-Expire";

    /**
     * 사용자 정보를 반환합니다.
     *
     * @param authInfo   - Auth Server 의 사용자 정보
     * @param memberInfo - Shop Server 의 사용자 정보
     * @return - 사용자 정보를 반환합니다.
     */
    @GetMapping
    public ResponseEntity<ShopResult<MemberResponse>> retrieveMember(final AuthInfo authInfo,
                                                                     final MemberInfo memberInfo) {
        MemberResponse memberResponse = new MemberResponse(authInfo, memberInfo);

        log.info("MemberResponse = {}", memberResponse);

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(memberResponse));
    }

    /**
     * Client 에서 받은 회원가입 Form 에서 입력한 정보로 회원가입을 하는 로직입니다.
     * 회원가입시 추천인을 입력했고, 해당 회원이 존재하면 추천인과 추천인을 입력한 회원은 적립금을 받습니다.
     *
     * @param signUpRequest - shop
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @PostMapping("/signup")
    public ResponseEntity<ShopResult<String>> doSignUp(@RequestBody @Valid final SignupRequest signUpRequest)
            throws JsonProcessingException {

        memberService.signUp(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 회원 탈퇴시 Soft 삭제를 위한 메소드 입니다.
     *
     * @return 응답 객체를 반환합니다.
     */
    @DeleteMapping
    public ResponseEntity<ShopResult<String>> withdraw(MemberInfo memberInfo,
                                                       HttpServletRequest request,
                                                       @RequestBody @Valid final MemberWithdrawRequest memberWithdraw) throws JsonProcessingException {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        memberService.withdraw(memberInfo, memberWithdraw, token);

        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    @PutMapping
    public ResponseEntity<ShopResult<String>> update(final MemberInfo memberInfo,
                                                     HttpServletRequest request,
                                                     @Valid @RequestBody final MemberUpdateRequest memberUpdateRequest) {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        UuidTokenResponse update = memberService.update(memberInfo, memberUpdateRequest, token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(update.getJwt());
        httpHeaders.set(JWT_EXPIRE, update.getExpiredDate().toString());

        return ResponseEntity.status(OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .headers(httpHeaders)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 선택한 회원에게 쿠폰을 지급하는 PostMapping 을 지원합니다.
     *
     * @param memberInfo         - 쿠폰을 등록할 회원의 정보입니다.
     * @param givenCouponRequest - 등록할 쿠폰 번호 정보를 가진 요청 객체입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @author 민아영
     * @since 1.0.0
     */
    @Operation(summary = "지급 쿠폰 생성",
            description = "회원이 쿠폰의 이름으로 쿠폰을 등록하면 지급 쿠폰이 생성됩니다.",
            parameters = {@Parameter(name = "memberInfo", description = "쿠폰을 등록하는 회원의 정보", required = true),
                    @Parameter(name = "givenCouponRequest", description = "등록할 쿠폰 이름을 가진 요청 객체", required = true)},
            responses = @ApiResponse(responseCode = "201",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShopResult.class)),
                    useReturnTypeSchema = true))
    @PostMapping("/coupons")
    public ResponseEntity<ShopResult<String>> createGivenCoupons(final MemberInfo memberInfo,
                                                                 @Valid @RequestBody final
                                                                 GivenCouponCreateRequest givenCouponRequest) {

        givenCouponService.createGivenCoupons(memberInfo, givenCouponRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 선택한 회원에게 지급된 쿠폰 목록을 조회하는 GetMapping 을 지원합니다.
     *
     * @param memberInfo - 쿠폰을 등록할 회원의 정보입니다.
     * @return 회원에게 지급된 쿠폰 목록을 가진 DTO 객체를 반환합니다.
     * @author 민아영
     * @since 1.0.0
     */
    @Operation(summary = "지급 쿠폰 조회",
               description = "회원이 자신에게 지급된 쿠폰을 조회합니다.",
               parameters = @Parameter(name = "memberInfo", description = "쿠폰을 등록하는 회원의 정보", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @GetMapping("/coupons")
    public ResponseEntity<ShopResult<PageEntity<GivenCouponResponse>>> retrieveGivenCoupons(
            final MemberInfo memberInfo,
            @RequestParam(value = "page", defaultValue = "1") final Integer page) {

        DefaultPageRequest pageRequest = new DefaultPageRequest(page - 1);

        PageEntity<GivenCouponResponse> givenCouponResponses
                = givenCouponService.retrieveGivenCoupons(memberInfo, pageRequest.getPageable());

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(givenCouponResponses));
    }

    /**
     * 한 회원이 상품에 대해 문의한 전체 상품 문의 글을 조회하는 GET Mapping 을 지원합니다.
     *
     * @param memberInfo - 상품 문의 글을 조회할 회원의 정보 입니다.
     * @param page       조회하려는 페이지 정보입니다.
     *                   (@PageableDefault - 기본값과 추가 설정을 할 수 있습니다.)
     * @return - List&lt;ProductInquiryByMemberResponse&gt; 를 담은 응답 객체를 반환 합니다.
     * @author 민아영
     * @since 1.0.0
     */
    @Operation(summary = "회원의 상품 문의 조회",
               description = "회원이 등록한 상품 문의에 대해 조회합니다.",
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @GetMapping("/product-inquiries")
    public ResponseEntity<ShopResult<PageEntity<ProductInquiryResponse>>> retrieveProductInquiry(
            final MemberInfo memberInfo, @RequestParam(value = "page", defaultValue = "1") final Integer page) {

        DefaultPageRequest pageRequest = new DefaultPageRequest(page - 1);

        PageEntity<ProductInquiryResponse> productInquiryResponses
                = productInquiryPostService.retrieveProductInquiryByMemberId(memberInfo, pageRequest.getPageable());

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(productInquiryResponses));
    }

    /**
     * 회원이 작성한 후기를 조회하기위한 컨트롤러입니다.
     *
     * @param memberInfo
     * @param page
     * @return
     */
    @Operation(summary = "회원의 후기",
               description = "회원이 등록한 후기에 대해 조회합니다.",
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = PageEntity.class)),
                                        useReturnTypeSchema = true))
    @GetMapping("/reviews")
    public ResponseEntity<PageEntity<ReviewResponse>> retrieveReviewsByMember(
        final MemberInfo memberInfo, @RequestParam(value = "page", defaultValue = "1") final Integer page) {

        DefaultPageRequest pageRequest = new DefaultPageRequest(page - 1);
        Page<ReviewResponse> responsePage =
            reviewService.retrieveReviewsByMember(memberInfo, pageRequest.getPageable());

        PageEntity<ReviewResponse> pageEntity = new PageEntity<>(responsePage.getNumber(),
                                                                 responsePage.getSize(),
                                                                 responsePage.getTotalPages(),
                                                                 responsePage.getContent());

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(pageEntity);
    }

}
