package com.nhnacademy.marketgg.server.controller.admin;

import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.request.category.CategoryCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.category.CategoryUpdateRequest;
import com.nhnacademy.marketgg.server.dto.response.category.CategoryRetrieveResponse;
import com.nhnacademy.marketgg.server.service.category.CategoryService;
import java.net.URI;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 카테고리 관리에 관련된 RestController 입니다.
 *
 * @author 김정민, 박세완
 * @version 1.0.0
 */
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    private static final String DEFAULT_CATEGORY = "/admin/categories";

    /**
     * 입력한 정보로 카테고리를 생성하는 POST Mapping 을 지원합니다.
     *
     * @param categoryCreateRequest - 카테고리를 생성하기 위한 DTO 입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @PostMapping
    public ResponseEntity<ShopResult<String>> createCategory(@RequestBody
                                                             @Valid final CategoryCreateRequest categoryCreateRequest) {

        categoryService.createCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .location(URI.create(DEFAULT_CATEGORY))
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 선택한 카테고리의 정보를 조회하는 GET Mapping 을 지원합니다.
     *
     * @param categoryId - 조회할 카테고리의 식별번호입니다.
     * @return 카테고리 한개의 정보를 담은 객체를 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<ShopResult<CategoryRetrieveResponse>> retrieveCategory(@PathVariable
                                                                                 @Size(min = 1, max = 6)
                                                                                 final String categoryId) {

        CategoryRetrieveResponse data = categoryService.retrieveCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY + "/" + categoryId))
                             .body(ShopResult.successWith(data));
    }

    /**
     * 입력한 정보로 선택한 카테고리 정보를 수정하는 PUT Mapping 을 지원합니다.
     *
     * @param categoryId      - 수정할 카테고리의 식별번호입니다.
     * @param categoryRequest - 카테고리를 수정하기 위한 DTO 입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<ShopResult<String>> updateCategory(@PathVariable
                                                             @Size(min = 1, max = 6) final String categoryId,
                                                             @Valid @RequestBody
                                                             final CategoryUpdateRequest categoryRequest) {

        categoryService.updateCategory(categoryId, categoryRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY + "/" + categoryId))
                             .body(ShopResult.successWithDefaultMessage());
    }

    /**
     * 선택한 카테고리를 삭제하는 DELETE Mapping 을 지원합니다.
     *
     * @param categoryId - 삭제할 카테고리의 식별번호입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ShopResult<String>> deleteCategory(@PathVariable
                                                             @Size(min = 1, max = 6) final String categoryId) {

        categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY + "/" + categoryId))
                             .body(ShopResult.successWithDefaultMessage());
    }

}
