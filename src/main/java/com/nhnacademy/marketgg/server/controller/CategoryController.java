package com.nhnacademy.marketgg.server.controller;

import com.nhnacademy.marketgg.server.dto.request.CategoryCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.CategoryUpdateRequest;
import com.nhnacademy.marketgg.server.dto.response.CategoryRetrieveResponse;
import com.nhnacademy.marketgg.server.service.CategoryService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shop/v1/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    private static final String DEFAULT_CATEGORY = "/shop/v1/admin/categories";

    @PostMapping
    ResponseEntity<Void> createCategory(@RequestBody final CategoryCreateRequest categoryCreateRequest) {
        categoryService.createCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .location(URI.create(DEFAULT_CATEGORY))
                             .contentType(MediaType.APPLICATION_JSON)
                             .build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryRetrieveResponse> retrieveCategory(@PathVariable String categoryId) {
        CategoryRetrieveResponse categoryResponse = categoryService.retrieveCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY + "/" + categoryId))
                             .body(categoryResponse);
    }

    @GetMapping
    public ResponseEntity<List<CategoryRetrieveResponse>> retrieveCategories() {
        List<CategoryRetrieveResponse> categoryResponses = categoryService.retrieveCategories();

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY))
                             .body(categoryResponses);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> updateCategory(@PathVariable final String categoryId,
                                               @RequestBody final CategoryUpdateRequest categoryRequest) {
        categoryService.updateCategory(categoryId, categoryRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY + "/" + categoryId))
                             .contentType(MediaType.APPLICATION_JSON)
                             .build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable final String categoryId) {
        categoryService.deleteCategory(categoryId);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_CATEGORY + "/" + categoryId))
                             .contentType(MediaType.APPLICATION_JSON)
                             .build();
    }

}
