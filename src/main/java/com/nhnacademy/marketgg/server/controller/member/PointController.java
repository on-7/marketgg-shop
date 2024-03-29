package com.nhnacademy.marketgg.server.controller.member;

import com.nhnacademy.marketgg.server.annotation.Auth;
import com.nhnacademy.marketgg.server.dto.PageEntity;
import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.DefaultPageRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 관련 포인트 내역 관리를 할 수 있는 Mapping 을 지원합니다.
 *
 * @author 박세완
 * @version 1.0.0
 */
@Auth
@RestController
@RequestMapping("/members/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    private static final String DEFAULT_MEMBER = "/members";

    /**
     * 지정한 회원의 포인트 내역을 반환합니다.
     *
     * @param memberInfo - 지정한 회원의 정보를 담은 객체입니다.
     * @return 지정한 회원의 포인트 내역을 List 로 반환합니다.
     * @since 1.0.0
     */
    @Operation(summary = "회원 포인트 내역 조회",
               description = "지정한 회원의 포인트 내역을 반환합니다.",
               parameters = { @Parameter(name = "memberInfo", description = "회원 정보", required = true),
                   @Parameter(name = "page", description = "페이지정보입니다.", required = true) },
               responses = @ApiResponse(responseCode = "200",
                                        content = @Content(mediaType = "application/json",
                                                           schema = @Schema(implementation = ShopResult.class)),
                                        useReturnTypeSchema = true))
    @GetMapping
    public ResponseEntity<PageEntity<PointRetrieveResponse>> retrievePointHistory(final MemberInfo memberInfo,
                                                                                  @RequestParam final Integer page) {

        DefaultPageRequest pageRequest = new DefaultPageRequest(page - 1);

        Page<PointRetrieveResponse> data =
            pointService.retrievePointHistories(memberInfo.getId(), pageRequest.getPageable());

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_MEMBER + "/points"))
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(new PageEntity<>(data.getNumber(),
                                                    data.getSize(),
                                                    data.getTotalPages(),
                                                    data.getContent()));
    }

}
