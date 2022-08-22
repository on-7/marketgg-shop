package com.nhnacademy.marketgg.server.controller.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.server.dto.PageEntity;
import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.elastic.dto.request.SearchRequest;
import com.nhnacademy.marketgg.server.elastic.dto.response.ProductListResponse;
import com.nhnacademy.marketgg.server.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 상품 Controller 입니다.
 *
 * @author 박세완
 * @version 1.0.0
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    private static final String DEFAULT_PRODUCT_URI = "products";

    /**
     * 전체 목록에서 검색한 상품 목록을 반환합니다.
     *
     * @return 전체 목록에서 검색한 상품 목록을 반환합니다.
     * @throws ParseException          파싱 도중 예외 처리입니다.
     * @throws JsonProcessingException Json 과 관련된 예외 처리입니다.
     * @since 1.0.0
     */
    @Operation(summary = "전체 상품 검색",
               description = "전체 목록에서 상품의 검색을 진행합니다.",
               parameters = @Parameter(name = "searchRequest", description = "검색 정보", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PostMapping("/search")
    public ResponseEntity<ShopResult<PageEntity<List<ProductListResponse>>>> searchProductList(
            @Valid @RequestBody final SearchRequest searchRequest)
            throws ParseException, JsonProcessingException {

        PageEntity<List<ProductListResponse>> productList = productService.searchProductList(searchRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_PRODUCT_URI + "/search"))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(productList));
    }

    /**
     * 카테고리 목록에서 검색한 상품 목록을 반환합니다.
     *
     * @param categoryId    - 지정한 카테고리 식별번호입니다.
     * @param searchRequest - 검색을 진행할 정보입니다.
     * @return 카테고리 목록에서 검색한 상품 목록을 반환합니다.
     * @throws ParseException          파싱 도중 예외 처리입니다.
     * @throws JsonProcessingException Json 과 관련된 예외 처리입니다.
     * @since 1.0.0
     */
    @Operation(summary = "카테고리 목록 내 상품 검색",
               description = "지정한 카테고리 목록 내에서 검색한 상품 목록을 검색합니다.",
               parameters = { @Parameter(name = "categoryId", description = "카테고리 식별번호", required = true),
                       @Parameter(name = "searchRequest", description = "검색 정보", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PostMapping("/categories/{categoryId}/search")
    public ResponseEntity<ShopResult<PageEntity<List<ProductListResponse>>>> searchProductListByCategory(
            @PathVariable @NotBlank @Size(min = 1, max = 6) final String categoryId,
            @Valid @RequestBody final SearchRequest searchRequest)
            throws ParseException, JsonProcessingException {

        PageEntity<List<ProductListResponse>> productList =
                productService.searchProductListByCategory(searchRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(
                                     DEFAULT_PRODUCT_URI + "/categories/" + categoryId + "/search"))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(productList));
    }

    /**
     * 카테고리 목록내에서 선택한 가격 정렬 옵션으로 정렬된 상품 목록을 반환합니다.
     *
     * @param categoryId    - 지정한 카테고리 식별번호입니다.
     * @param option        - 검색한 목록을 정렬할 가격옵션을 정렬 값입니다.
     * @param searchRequest - 검색을 진행할 정보입니다.
     * @return 카테고리 목록내에서 선택한 가격 정렬 옵션으로 정렬된 상품 목록을 반환합니다.
     * @throws ParseException          파싱 도중 예외 처리입니다.
     * @throws JsonProcessingException Json 과 관련된 예외 처리입니다.
     * @since 1.0.0
     */
    @Operation(summary = "옵션에 따른 상품 목록조회",
               description = "지정한 카테고리 내에서 지정한 가격 옵션에따른 상품 목록을 검색합니다.",
               parameters = { @Parameter(name = "categoryId", description = "카테고리 식별번호", required = true),
                       @Parameter(name = "option", description = "지정한 옵션의 값", required = true),
                       @Parameter(name = "searchRequest", description = "검색 정보", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @PostMapping("/categories/{categoryId}/sort-price/{option}/search")
    public ResponseEntity<ShopResult<PageEntity<List<ProductListResponse>>>> searchProductListByPrice(
            @PathVariable @NotBlank @Size(min = 1, max = 6) final String categoryId,
            @PathVariable @NotBlank @Min(1) final String option,
            @Valid @RequestBody final SearchRequest searchRequest)
            throws ParseException, JsonProcessingException {

        PageEntity<List<ProductListResponse>> productList =
                productService.searchProductListByPrice(option, searchRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(
                                     DEFAULT_PRODUCT_URI + "/categories/" + categoryId + "/sort-price/" + option))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWith(productList));
    }

}
