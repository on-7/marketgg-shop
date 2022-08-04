package com.nhnacademy.marketgg.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.server.constant.CustomerServicePostReason;
import com.nhnacademy.marketgg.server.dto.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.customerservice.PostRequest;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponse;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponseForDetail;
import com.nhnacademy.marketgg.server.elastic.dto.request.SearchRequest;
import com.nhnacademy.marketgg.server.service.PostService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자의 고객센터에 관련된 Rest Controller 입니다.
 *
 * @version 1.0.0
 */
// @RoleCheck(accessLevel = Role.ROLE_USER)
@RestController
@RequestMapping("/customer-services")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private static final String DEFAULT_POST = "/customer-services";

    /**
     * 1:1 문의를 등록하는 POST Mapping 을 지원합니다.
     * 1:1 문의 = one-to-one inquiry
     * one-to-one 을 축약하여 oto 으로 표현하였습니다.
     *
     * @param memberInfo  - 1:1 문의를 등록하는 회원의 정보입니다.
     * @param postRequest - 1:1 문의를 등록하기 위한 CustomerServicePostDto 객체입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @PostMapping
    public ResponseEntity<Void> createOtoInquiry(@Valid @RequestBody final PostRequest postRequest,
                                                 final MemberInfo memberInfo) {

        postService.createPost(memberInfo.getId(), postRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .location(URI.create(DEFAULT_POST))
                             .contentType(MediaType.APPLICATION_JSON)
                             .build();
    }

    /**
     * 지정한 게시글의 상세정보를 조회할 수 있는 GET Mapping 을 지원합니다.
     *
     * @param boardNo - 조회할 게시글의 식별번호입니다.
     * @return 지정한 게시글의 상세 정보를 담은 응답객체를 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/{boardNo}")
    public ResponseEntity<PostResponseForDetail> retrievePost(@PathVariable final Long boardNo) {
        PostResponseForDetail response = postService.retrievePost(boardNo);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/" + boardNo))
                             .body(response);
    }

    /**
     * 선택한 게시글을 조회하는 GET Mapping 을 지원합니다.
     *
     * @param boardNo - 선택한 게시글의 식별번호입니다.
     * @return 조회한 1:1 문의 단건의 정보를 담은 객체를 반환합니다.
     * @since 1.0.
     */
    @GetMapping("/oto-inquiries/{boardNo}")
    public ResponseEntity<PostResponseForOtoInquiry> retrieveOwnOtoInquiry(@PathVariable final Long boardNo,
                                                                           final MemberInfo memberInfo) {

        PostResponseForOtoInquiry inquiryResponse = postService.retrieveOwnOtoInquiryPost(boardNo, memberInfo.getId());

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/oto-inquiries/" + boardNo))
                             .body(inquiryResponse);
    }

    /**
     * 카테고리에 따라 고객센터 게시글 목록을 조회하는 GET Mapping 을 지원합니다.
     *
     * @param categoryCode - 조회할 게시글 목록의 카테고리 식별번호입니다.
     * @param page         - 페이징 처리를 위한 페이지 번호입니다.
     * @return 게시글 목록을 List 로 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/categories/{categoryCode}")
    public ResponseEntity<List<PostResponse>> retrievePostList(@PathVariable final String categoryCode,
                                                               @RequestParam final Integer page) {

        List<PostResponse> responses = postService.retrievePostList(categoryCode, page);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/categories/" + categoryCode))
                             .body(responses);
    }

    /**
     * 회원의 1:1 문의 목록을 조회하는 GET Mapping 을 지원합니다.
     *
     * @param page       - 페이징 처리를 위한 페이지 번호입니다.
     * @param memberInfo - 목록을 조회하는 회원의 정보가 담긴 객체입니다.
     * @return 조회한 1:1 문의 목록을 List 로 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/oto-inquiries")
    public ResponseEntity<List<PostResponse>> retrieveOwnPostList(@RequestParam final Integer page,
                                                                  final MemberInfo memberInfo) {

        List<PostResponse> responses = postService.retrieveOwnPostList(page, "702", memberInfo.getId());

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/oto-inquiries"))
                             .body(responses);
    }

    /**
     * 지정한 게시판 타입의 전체 목록을 검색한 결과를 반환합니다.
     *
     * @param categoryCode  - 검색을 진행 할 게시판 타입입니다.
     * @param searchRequest - 검색을 진행 할 검색 정보입니다.
     * @return 검색정보로 검색한 결과 목록 응답객체를 반환합니다.
     * @throws ParseException          파싱도중 예외처리입니다.
     * @throws JsonProcessingException JSON 관련 파싱처리 도중 예외처리입니다.
     * @since 1.0.0
     */
    @PostMapping("/categories/{categoryCode}/search")
    public ResponseEntity<List<PostResponse>> searchPostListForCategory(@PathVariable final String categoryCode,
                                                                               @RequestBody final SearchRequest searchRequest)
            throws ParseException, JsonProcessingException {

        List<PostResponse> responses = postService.searchForCategory(categoryCode, searchRequest);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/categories/" + categoryCode + "/search"))
                             .body(responses);
    }

    /**
     * 선택한 1:1 문의를 삭제하는 DELETE Mapping 을 지원합니다.
     *
     * @param boardNo - 선택한 1:1 문의의 식별번호입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @DeleteMapping("/oto-inquiries/{boardNo}")
    public ResponseEntity<Void> deleteOtoInquiry(@PathVariable final Long boardNo) {
        postService.deletePost(boardNo);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                             .location(URI.create(DEFAULT_POST + "/oto-inquiries/" + boardNo))
                             .contentType(MediaType.APPLICATION_JSON)
                             .build();
    }

    /**
     * 고객센터 게시글의 사유 목록을 불러오는 GET Mapping 을 지원합니다.
     *
     * @return 사유 목록을 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/reasons")
    public ResponseEntity<List<String>> retrieveAllReasonValues() {
        List<String> reasons = Arrays.stream(CustomerServicePostReason.values())
                                     .map(CustomerServicePostReason::reason)
                                     .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/reasons"))
                             .body(reasons);
    }

}
