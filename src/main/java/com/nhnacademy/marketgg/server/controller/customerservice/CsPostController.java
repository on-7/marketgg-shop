package com.nhnacademy.marketgg.server.controller.customerservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.server.annotation.Role;
import com.nhnacademy.marketgg.server.annotation.RoleCheck;
import com.nhnacademy.marketgg.server.constant.OtoReason;
import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.customerservice.PostRequest;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponse;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponseForDetail;
import com.nhnacademy.marketgg.server.elastic.dto.request.SearchRequest;
import com.nhnacademy.marketgg.server.service.post.PostService;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
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

/**
 * 사용자의 고객센터에 관련된 Rest Controller 입니다.
 *
 * @author 박세완, 김정민
 * @version 1.0.0
 */
@RoleCheck(accessLevel = Role.ROLE_USER)
@RestController
@RequestMapping("/customer-services")
@RequiredArgsConstructor
public class CsPostController {

    private final PostService postService;

    private static final String DEFAULT_POST = "/customer-services";
    private static final Integer PAGE_SIZE = 10;

    /**
     * 게시글을 등록하는 POST Mapping 을 지원합니다.
     *
     * @param memberInfo  - 게시글을 등록하는 회원의 정보입니다.
     * @param postRequest - 게시글을 등록하기 위한 PostRequest 객체입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @PostMapping
    public ResponseEntity<Void> createPost(@Valid @RequestBody final PostRequest postRequest,
                                           final MemberInfo memberInfo) {

        postService.createPost(postRequest, memberInfo);

        return ResponseEntity.status(HttpStatus.CREATED)
                             .location(URI.create(DEFAULT_POST))
                             .contentType(MediaType.APPLICATION_JSON)
                             .build();
    }

    /**
     * 카테고리에 따라 고객센터 게시글 목록을 조회하는 GET Mapping 을 지원합니다.
     *
     * @param categoryId - 조회할 게시글 목록의 카테고리 식별번호입니다.
     * @param page       - 페이징 처리를 위한 페이지 번호입니다.
     * @param memberInfo - 로그인 한 회원의 정보입니다.
     * @return 게시글 목록을 List 로 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<PostResponse>> retrievePostList(@PathVariable final String categoryId,
                                                               @RequestParam final Integer page,
                                                               final MemberInfo memberInfo) {

        List<PostResponse> responses = postService.retrievePostList(categoryId, page, memberInfo);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/categories/" + categoryId))
                             .body(responses);
    }

    /**
     * 지정한 게시글의 상세정보를 조회할 수 있는 GET Mapping 을 지원합니다.
     *
     * @param postId     - 조회할 게시글의 식별번호입니다.
     * @param memberInfo - 로그인 한 회원의 정보입니다.
     * @return 지정한 게시글의 상세 정보를 담은 응답객체를 반환합니다.
     * @since 1.0.0
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseForDetail> retrievePost(@PathVariable final Long postId,
                                                              final MemberInfo memberInfo)
        throws JsonProcessingException {

        PostResponseForDetail response = postService.retrievePost(postId, memberInfo);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/" + postId))
                             .body(response);
    }

    /**
     * 지정한 게시판 타입의 전체 목록에서 검색한 결과를 반환합니다.
     *
     * @param categoryId - 검색을 진행 할 게시판 타입입니다.
     * @param keyword    - 검색을 진행 할 키워드입니다.
     * @param page       - 검색을 진행 할 페이지 정보입니다.
     * @param memberInfo - 검색을 진행 할 회원의 정보입니다.
     * @return 검색정보로 검색한 결과 목록 응답객체를 반환합니다.
     * @throws ParseException          파싱도중 예외처리입니다.
     * @throws JsonProcessingException JSON 관련 파싱처리 도중 예외처리입니다.
     * @since 1.0.0
     */

    @GetMapping("/categories/{categoryId}/search")
    public ResponseEntity<List<PostResponse>> searchPostListForCategory(@PathVariable final String categoryId,
                                                                        @RequestParam final String keyword,
                                                                        @RequestParam final Integer page,
                                                                        final MemberInfo memberInfo)
        throws ParseException, JsonProcessingException {

        List<PostResponse> responses = postService.searchForCategory(categoryId, new SearchRequest(keyword, page, PAGE_SIZE), memberInfo);

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/categories/" + categoryId + "/search"))
                             .body(responses);
    }

    /**
     * 선택한 1:1 문의를 삭제하는 DELETE Mapping 을 지원합니다.
     *
     * @param categoryId - 삭제를 진행 할 게시판 타입입니다.
     * @param postId     - 선택한 1:1 문의의 식별번호입니다.
     * @param memberInfo - 삭제를 진행 할 회원의 정보입니다.
     * @return Mapping URI 를 담은 응답 객체를 반환합니다.
     * @since 1.0.0
     */
    @DeleteMapping("/categories/{categoryId}/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable final String categoryId, @PathVariable final Long postId,
                                           final MemberInfo memberInfo) {
        postService.deletePost(categoryId, postId, memberInfo);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                             .location(URI.create(DEFAULT_POST + postId))
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
    public ResponseEntity<List<String>> retrieveReasonList() {
        List<String> reasons = Arrays.stream(OtoReason.values())
                                     .map(OtoReason::reason)
                                     .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                             .location(URI.create(DEFAULT_POST + "/reasons"))
                             .body(reasons);
    }

}