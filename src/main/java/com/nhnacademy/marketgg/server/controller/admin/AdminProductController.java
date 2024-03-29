package com.nhnacademy.marketgg.server.controller.admin;

import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.request.product.ProductCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.product.ProductUpdateRequest;
import com.nhnacademy.marketgg.server.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 상품 관리를 위한 RestController 입니다.
 *
 * @author 조현진
 * @version 1.0.0
 */
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    private static final String DEFAULT_ADMIN_PRODUCT = "/admin/products";

    /**
     * 상품 생성을 위한 POST Mapping 을 지원합니다.
     *
     * @param productRequest - 상품 생성을 위한 DTO 입니다.
     * @param image          - 상품 등록시 필요한 image 입니다. MultipartFile 타입 입니다.
     * @return - Mapping URI 를 담은 응답 객체를 반환합니다.
     * @throws IOException - IOException 을 던집니다.
     * @since 1.0.0
     */

    @Operation(summary = "관리자의 상품 관리",
               description = "상품에 관한 정보를 받고, 데이터베이스에 해당 정보를 영속화합니다.",
               parameters = {
                       @Parameter(name = "productRequest", description = "상품 등록에 필요한 정보를 담은 객체", required = true),
                       @Parameter(name = "image", description = "상품 목록에 보이는 썸네일용 이미지", required = true) },
               responses = @ApiResponse(responseCode = "201",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))

    @PostMapping(consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ShopResult<String>> createProduct(
            @RequestPart @Valid final ProductCreateRequest productRequest,
            BindingResult bindingResult,
            @RequestPart final MultipartFile image) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        this.productService.createProduct(productRequest, image);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .location(URI.create(DEFAULT_ADMIN_PRODUCT))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 상품 수정을 위한 PUT Mapping 을 지원합니다.
     *
     * @param productRequest - 상품 수정을 위한 DTO 입니다.
     * @param image          - 상품 수정을 위한 MultipartFile 입니다.
     * @param productId      - 상품 수정을 위한 PK 입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @throws IOException - 입출력에서 문제 발생 시 예외를 던집니다.
     * @since 1.0.0
     */

    @Operation(summary = "관리자의 상품 관리",
               description = "상품에 관한 정보를 받고, 데이터베이스에 해당 정보를 영속화합니다.",
               parameters = {
                       @Parameter(name = "productRequest", description = "상품 수정에 필요한 정보를 담은 객체", required = true),
                       @Parameter(name = "image", description = "상품 목록에 보이는 썸네일용 이미지", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))

    @PutMapping("/{productId}")
    public ResponseEntity<ShopResult<String>> updateProduct(
            @RequestPart @Valid final ProductUpdateRequest productRequest,
            BindingResult bindingResult,
            @RequestPart final MultipartFile image,
            @PathVariable final Long productId) throws IOException {

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        this.productService.updateProduct(productRequest, image, productId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_ADMIN_PRODUCT + "/" + productId))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 상품 소프트 삭제를 위한 POST Mapping 를 지원합니다.
     * Delete Query 가 날아가는 것이 아닌, 상품의 상태 값을 '삭제'로 변경 합니다.
     *
     * @param productId - 상품 삭제를 위한 PK 입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */

    @Operation(summary = "관리자의 상품 관리",
               description = "상품 번호를 받아서 해당 번호에 해당하는 상품을 소프트 삭제합니다.",
               parameters = @Parameter(name = "productId", description = "상품 번호", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))

    @DeleteMapping("/{productId}")
    public ResponseEntity<ShopResult<String>> deleteProduct(@PathVariable final Long productId) {
        this.productService.deleteProduct(productId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_ADMIN_PRODUCT + "/" + productId))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 상품 상태가 삭제인 값을 복구합니다.
     *
     * @param productId - 상품 복구를 위한 기본키입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     */

    @Operation(summary = "관리자의 상품 관리",
               description = "상품 번호를 받아서 해당 번호에 해당하는 상품을 복원합니다.",
               parameters = @Parameter(name = "productId", description = "상품 번호", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))

    @PostMapping("/{productId}/restore")
    public ResponseEntity<ShopResult<String>> restoreProduct(@PathVariable final Long productId) {
        this.productService.restoreProduct(productId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_ADMIN_PRODUCT + "/" + productId))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(ShopResult.successWithDefaultMessage());
    }

}
