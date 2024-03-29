package com.nhnacademy.marketgg.server.controller.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.server.dto.PageEntity;
import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.info.AuthInfo;
import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.DefaultPageRequest;
import com.nhnacademy.marketgg.server.dto.request.order.CartOrderRequest;
import com.nhnacademy.marketgg.server.dto.request.order.OrderCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.order.OrderUpdateStatusRequest;
import com.nhnacademy.marketgg.server.dto.response.order.OrderDetailRetrieveResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderFormResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderPaymentKey;
import com.nhnacademy.marketgg.server.dto.response.order.OrderRetrieveResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderToPayment;
import com.nhnacademy.marketgg.server.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * 주문에 관련된 Rest Controller 입니다.
 *
 * @author 김정민
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private static final String ORDER_PREFIX = "/orders";

    /**
     * 주문을 등록하는 POST Mapping 을 지원합니다.
     *
     * @param orderCreateRequest - 주문을 등록하기 위한 정보입니다.
     * @param memberInfo         - 주문하는 회원의 정보입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "주문 등록",
               description = "주문 등록을 위해 들어온 값들을 검증하고 주문 등록을 처리합니다.",
               parameters = { @Parameter(name = "orderCreateRequest", description = "주문 등록 요청 데이터", required = true),
                       @Parameter(name = "memberInfo", description = "주문하는 회원 정보", required = true) },
               responses = @ApiResponse(responseCode = "201",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PostMapping
    public ResponseEntity<ShopResult<OrderToPayment>> createOrder(@RequestBody final
                                                                  OrderCreateRequest orderCreateRequest,
                                                                  final MemberInfo memberInfo)
            throws JsonProcessingException {

        log.info("createOrder method started");
        log.info("orderRequest: {}", orderCreateRequest);

        OrderToPayment data = orderService.createOrder(orderCreateRequest, memberInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .location(URI.create("/payments/verify"))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(data));
    }

    /**
     * 주문서 작성에 필요한 정보를 취합하여 제공하는 GET Mapping 을 지원합니다.
     *
     * @param cartRequest - 장바구니에서 주문할 상품 ID 목록 입니다.
     * @param memberInfo  - 주문하는 회원의 정보입니다.
     * @param authInfo    - 주문하는 회원의 auth 정보입니다.
     * @return 회원의 정보를 토대로 주문서 작성에 필요한 값들과 상품목록을 취합하여 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "주문서 정보 요청",
               description = "주문서 입력폼에 필요한 정보들을 취합하여 반환합니다.",
               parameters = { @Parameter(name = "cartResponse", description = "장바구니에서 선택한 주문할 상품", required = true),
                       @Parameter(name = "memberInfo", description = "주문하는 회원 정보", required = true),
                       @Parameter(name = "AuthInfo", description = "주문하는 회원 auth 정보", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PostMapping("/order-form")
    public ResponseEntity<ShopResult<OrderFormResponse>> retrieveOrderForm(@RequestBody final
                                                                           CartOrderRequest cartRequest,
                                                                           final MemberInfo memberInfo,
                                                                           final AuthInfo authInfo) {

        log.info("retrieveOrderForm method started");
        log.info("cartOrderRequest: {}", cartRequest);

        OrderFormResponse data = orderService.retrieveOrderForm(cartRequest.getId(), memberInfo, authInfo);

        return ResponseEntity.status(HttpStatus.OK)
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(data));
    }

    /**
     * 주문 내역에서 볼 수 있는 주문 목록을 조회하는 GET Mapping 을 지원합니다.
     * 관리자는 모든 회원에 대한 주문, 삭제된 주문 내역도 볼 수 있습니다.
     *
     * @param memberInfo - 조회하는 회원의 정보입니다.
     * @return 주문 목록을 List 로 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "주문 목록 조회",
               description = "주문 목록을 조회합니다.(회원이라면 본인의 목록만 조회 가능합니다.)",
               parameters = @Parameter(name = "memberInfo", description = "주문하는 회원 정보", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @GetMapping
    public ResponseEntity<ShopResult<PageEntity<OrderRetrieveResponse>>> retrieveOrderList(final MemberInfo memberInfo,
                                                                                           @RequestParam(value = "page",
                                                                                                         defaultValue = "1") final Integer page) {
        log.info("retrieveOrderList method started");

        DefaultPageRequest pageRequest = new DefaultPageRequest(page - 1); // offset 때문에 -1 처리

        Page<OrderRetrieveResponse> data = orderService.retrieveOrderList(memberInfo, pageRequest.getPageable());

        PageEntity<OrderRetrieveResponse> pageEntity = new PageEntity<>(data.getNumber(), data.getSize(),
                                                                        data.getTotalPages(), data.getContent());

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(ORDER_PREFIX))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(pageEntity));
    }

    /**
     * 주문 상세 조회를 할 수 있는 GET Mapping 을 지원합니다.
     *
     * @param orderId    - 상세 조회할 주문의 식별번호입니다.
     * @param memberInfo - 조회하는 회원의 정보입니다.
     * @return 상세 조회한 주문을 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "주문 상세 조회",
               description = "지정한 주문의 상세 정보를 조회합니다.(회원이라면 본인의 상세 정보만 조회 가능합니다.)",
               parameters = { @Parameter(name = "orderId", description = "주문 식별번호", required = true),
                       @Parameter(name = "memberInfo", description = "주문하는 회원 정보", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @GetMapping("/{orderId}")
    public ResponseEntity<ShopResult<OrderDetailRetrieveResponse>> retrieveOrderDetail(@PathVariable final Long orderId,
                                                                                       final MemberInfo memberInfo) throws JsonProcessingException {

        log.info("retrieveOrderDetail method started");
        log.info("orderId: {}", orderId);

        OrderDetailRetrieveResponse data = orderService.retrieveOrderDetail(orderId, memberInfo);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(ORDER_PREFIX + "/" + orderId))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(data));
    }

    /**
     * 주문의 상태를 변경하는 PATCH Mapping 을 지원합니다.
     *
     * @param orderId - 상태를 변경할 주문의 식별번호입니다.
     * @param status  - 변경할 상태의 값입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "주문 상태 변경",
               description = "주문의 상태를 변경합니다.",
               parameters = { @Parameter(name = "orderId", description = "주문 식별번호", required = true),
                       @Parameter(name = "status", description = "변경할 상태값", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ShopResult<String>> updateStatus(@PathVariable final Long orderId,
                                                           @RequestBody final OrderUpdateStatusRequest status) {

        log.info("updateStatus method started");
        log.info("status: {}", status);

        orderService.updateStatus(orderId, status);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(ORDER_PREFIX + "/" + orderId + "/status"))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 주문에 운송장 번호 발급을 요청하는 PATCH Mapping 을 지원합니다.
     *
     * @param orderId - 운송장 번호를 발급하려는 주문의 식별번호입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @throws JsonProcessingException - Json 컨텐츠를 처리할 때 발생하는 모든 문제에 대한 예외처리입니다.
     * @since 1.0.0
     */
    @Operation(summary = "운송장번호 발급 요청",
               description = "주문의 운송장번호 발급을 요청합니다.",
               parameters = @Parameter(name = "orderId", description = "주문 식별번호", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PatchMapping("/{orderId}/delivery")
    public ResponseEntity<ShopResult<String>> createTrackingNo(@PathVariable final
                                                               Long orderId) throws JsonProcessingException {

        log.info("createTrackingNo method started");

        orderService.createTrackingNo(orderId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(ORDER_PREFIX + "/" + orderId + "/delivery"))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 주문을 취소할 수 있는 PUT Mapping 을 지원합니다.(소프트 삭제)
     *
     * @param orderId - 취소할 주문의 식별번호입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "주문 취소",
               description = "지정한 주문을 취소합니다.(소프트 삭제)",
               parameters = @Parameter(name = "orderId", description = "주문 식별번호", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PatchMapping("/{orderId}")
    public ResponseEntity<ShopResult<String>> cancelOrder(@PathVariable final Long orderId) {
        log.info("cancelOrder method started");

        orderService.cancelOrder(orderId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(ORDER_PREFIX + "/" + orderId))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 주문에 대한 paymentKey 조회를 할 수 있는 GET Mapping 을 지원합니다.
     *
     * @param orderId - paymentKey 를 조회할 주문의 식별번호입니다.
     * @return 조회한 paymentKey 를 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "주문의 paymentKey 조회",
               description = "지정한 주문의 paymentKey 를 조회합니다.",
               parameters = { @Parameter(name = "orderId", description = "주문 식별번호", required = true),
                       @Parameter(name = "memberInfo", description = "조회하는 회원 정보", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @GetMapping("/{orderId}/paymentKey")
    public ResponseEntity<ShopResult<OrderPaymentKey>> retrieveOrderPaymentKey(@PathVariable final Long orderId,
                                                                               final MemberInfo memberInfo) {
        log.info("retrieveOrderPaymentKey method started");

        OrderPaymentKey data = orderService.retrieveOrderPaymentKey(orderId, memberInfo);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(ORDER_PREFIX + "/" + orderId + "/paymentKey"))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(data));
    }

}
