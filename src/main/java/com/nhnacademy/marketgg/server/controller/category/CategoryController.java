package com.nhnacademy.marketgg.server.controller.category;

import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.response.category.CategoryRetrieveResponse;
import com.nhnacademy.marketgg.server.service.category.CategoryService;
import java.net.URI;
import java.util.List;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자의 카테고리 조회를 위한 컨트롤러입니다.
 *
 * @author 김정민, 박세완, 조현진
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private static final String DEFAULT_CATEGORY = "/categories";

    /**
     * 전체 카테고리 목록을 조회하는 GET Mapping 을 지원합니다.
     *
     * @return 카테고리 전체 목록을 List 로 반환합니다.
     * @since 1.0.0
     */
    @GetMapping
    public ResponseEntity<ShopResult<List<CategoryRetrieveResponse>>> retrieveCategories() {
        List<CategoryRetrieveResponse> data = categoryService.retrieveCategories();

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY))
                             .body(ShopResult.successWith(data));
    }

    /**
     * 카레고리 분류표에 따라 카테고리를 조회하는 GET Mapping 을 지원합니다.
     *
     * @param categorizationId - 카테고리 분류표 식별번호입니다.
     * @return 해당하는 카테고리 분류표에 따른 카테고리들을 List 로 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/categorizations/{categorizationId}")
    public ResponseEntity<ShopResult<List<CategoryRetrieveResponse>>> retrieveCategoriesByCategorization(
        @PathVariable @Size(min = 1, max = 3) final String categorizationId) {

        List<CategoryRetrieveResponse> data = categoryService.retrieveCategoriesByCategorization(
            categorizationId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY + "/" + categorizationId))
                             .body(ShopResult.successWith(data));
    }
}
