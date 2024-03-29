package com.nhnacademy.marketgg.server.controller.admin;

import com.nhnacademy.marketgg.server.dto.PageEntity;
import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.response.point.PointRetrieveResponse;
import com.nhnacademy.marketgg.server.service.point.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 관련 포인트 내역 관리를 할 수 있는 Mapping 을 지원합니다.
 *
 * @author 박세완
 * @version 1.0.0
 */
@RestController
@RequestMapping("/admin/points")
@RequiredArgsConstructor
public class AdminPointController {

    private final PointService pointService;

    private static final String DEFAULT_ADMIN = "/admin";

    /**
     * 전체 회원의 포인트 내역을 반환합니다.
     *
     * @return 전체 회원의 포인트 내역을 List 로 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "전체 회원 포인트 내역 조회",
               description = "전제 회원의 포인트 내역목록을 반환합니다.",
               parameters = @Parameter(name = "page", description = "페이지 정보입니다.", required = true),
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @GetMapping
    public ResponseEntity<PageEntity<PointRetrieveResponse>> adminRetrievePointHistory(
            @RequestParam final Integer page) {
        Page<PointRetrieveResponse> data = pointService.adminRetrievePointHistories(PageRequest.of(page, 10));

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_ADMIN + "/points"))
                             .body(new PageEntity<>(data.getNumber(),
                                                    data.getSize(),
                                                    data.getTotalPages(),
                                                    data.getContent()));
    }

}
