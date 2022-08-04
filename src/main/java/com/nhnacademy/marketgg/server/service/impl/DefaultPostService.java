package com.nhnacademy.marketgg.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.server.auth.AuthRepository;
import com.nhnacademy.marketgg.server.dto.MemberInfo;
import com.nhnacademy.marketgg.server.dto.MemberNameResponse;
import com.nhnacademy.marketgg.server.dto.request.customerservice.PostRequest;
import com.nhnacademy.marketgg.server.dto.request.customerservice.PostStatusUpdateRequest;
import com.nhnacademy.marketgg.server.dto.response.customerservice.CommentReady;
import com.nhnacademy.marketgg.server.dto.response.customerservice.CommentResponse;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponse;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponseForDetail;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponseForReady;
import com.nhnacademy.marketgg.server.elastic.document.ElasticBoard;
import com.nhnacademy.marketgg.server.elastic.dto.request.SearchRequest;
import com.nhnacademy.marketgg.server.elastic.repository.ElasticBoardRepository;
import com.nhnacademy.marketgg.server.elastic.repository.SearchRepository;
import com.nhnacademy.marketgg.server.entity.Category;
import com.nhnacademy.marketgg.server.entity.CustomerServicePost;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.exception.NotFoundException;
import com.nhnacademy.marketgg.server.exception.category.CategoryNotFoundException;
import com.nhnacademy.marketgg.server.exception.customerservicecomment.CustomerServiceCommentNotFoundException;
import com.nhnacademy.marketgg.server.exception.customerservicepost.CustomerServicePostNotFoundException;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.repository.category.CategoryRepository;
import com.nhnacademy.marketgg.server.repository.customerservicecomment.CustomerServiceCommentRepository;
import com.nhnacademy.marketgg.server.repository.customerservicepost.CustomerServicePostRepository;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.service.PostService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultPostService implements PostService {

    private final CustomerServicePostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final CustomerServiceCommentRepository commentRepository;
    private final ElasticBoardRepository elasticBoardRepository;
    private final SearchRepository searchRepository;
    private final AuthRepository authRepository;

    private static final String NOTICE_CODE = "701";
    private static final String OTO_CODE = "702";
    private static final String FAQ_CODE = "703";
    private static final Integer PAGE_SIZE = 10;

    @Transactional
    @Override
    public void createPost(final PostRequest postRequest, final MemberInfo memberInfo) {
        if (Boolean.TRUE.equals(this.isAccess(memberInfo, postRequest.getCategoryCode(), "create"))) {
            Member member = memberRepository.findById(memberInfo.getId()).orElseThrow(MemberNotFoundException::new);
            Category category = categoryRepository.findById(postRequest.getCategoryCode()).orElseThrow(
                    CategoryNotFoundException::new);
            CustomerServicePost post = new CustomerServicePost(member, category, postRequest);

            CustomerServicePost savePost = postRepository.save(post);
            elasticBoardRepository.save(new ElasticBoard(savePost));
        }
    }

    @Override
    public List<PostResponse> retrievePostList(final String categoryCode, final Integer page,
                                               final MemberInfo memberInfo) {
        if (Boolean.TRUE.equals(this.isAccess(memberInfo, categoryCode, "retrieveList"))) {
            return postRepository.findPostsByCategoryId(PageRequest.of(page, PAGE_SIZE), categoryCode).getContent();
        }
        return postRepository.findPostByCategoryAndMember(PageRequest.of(page, PAGE_SIZE), categoryCode,
                                                          memberInfo.getId()).getContent();
    }

    @Override
    public PostResponseForDetail retrievePost(final Long postNo, final MemberInfo memberInfo) {
        CustomerServicePost post =
                postRepository.findById(postNo).orElseThrow(CustomerServiceCommentNotFoundException::new);
        if (post.getCategory().getId().compareTo(OTO_CODE) == 0 || !memberInfo.isAdmin()) {
            return this.convertToDetail(postRepository.findOwnOtoInquiry(postNo, memberInfo.getId()));
        }
        return this.convertToDetail(postRepository.findByBoardNo(postNo));
    }

    @Override
    public List<PostResponse> searchForCategory(final String categoryCode, final SearchRequest searchRequest,
                                                final MemberInfo memberInfo)
            throws ParseException, JsonProcessingException {

        if(Boolean.TRUE.equals(isAccess(memberInfo, categoryCode, "search"))) {
            return searchRepository.searchBoardWithCategoryCode(categoryCode, searchRequest, "board");
        }
        return List.of();
    }

    @Override
    public List<PostResponse> searchForOption(final String categoryCode, final SearchRequest searchRequest,
                                              final String optionType, final String option)
            throws JsonProcessingException, ParseException {

        return searchRepository.searchBoardWithOption(categoryCode, option, searchRequest, optionType);
    }

    @Override
    public void updatePost(final String categoryCode, final Long postNo, final PostRequest postRequest) {
        if(categoryCode.compareTo(OTO_CODE) != 0) {
            CustomerServicePost post = postRepository.findById(postNo).orElseThrow(
                    CustomerServicePostNotFoundException::new);
            post.updatePost(postRequest);

            CustomerServicePost savePost = postRepository.save(post);
            elasticBoardRepository.save(new ElasticBoard(savePost));
        }
    }

    @Override
    public void updateOtoInquiryStatus(final Long postNo, final PostStatusUpdateRequest statusUpdateRequest) {
        CustomerServicePost post = postRepository.findById(postNo).orElseThrow(CustomerServicePostNotFoundException::new);
        post.updatePostStatus(statusUpdateRequest.getStatus());

        CustomerServicePost savePost = postRepository.save(post);
        elasticBoardRepository.save(new ElasticBoard(savePost));
    }

    @Override
    public void deletePost(final String categoryCode, final Long postNo, final MemberInfo memberInfo) {
        if(isAccess(memberInfo, categoryCode, "memberDelete") || isAccess(memberInfo, categoryCode, "adminDelete")) {
            postRepository.deleteById(postNo);
            elasticBoardRepository.deleteById(postNo);
            commentRepository.deleteAllByCustomerServicePost_Id(postNo);
        }
    }

    private Boolean isAccess(final MemberInfo memberInfo, final String categoryCode, final String option) {
        List<String> otoNonExistList = List.of(NOTICE_CODE, FAQ_CODE);
        List<String> otoExistList = List.of(OTO_CODE);

        switch (option) {
            case "create": {
                return (memberInfo.isAdmin()) || !otoNonExistList.contains(categoryCode);
            }
            case "retrieveList":
            case "search": {
                return memberInfo.isAdmin() || !otoExistList.contains(categoryCode);
            }
            case "memberDelete": {
                return !memberInfo.isAdmin() && !otoNonExistList.contains(categoryCode);
            }
            case "adminDelete": {
                return memberInfo.isAdmin() && otoExistList.contains(categoryCode);
            }
            default:
                throw new NotFoundException("허용되지 않는 옵션잆니다. 값: " + option);
        }
    }

    private PostResponseForDetail convertToDetail(final PostResponseForReady response) throws JsonProcessingException {
        List<String> uuidList = new ArrayList<>();
        for(CommentReady ready : response.getCommentReady()) {
            uuidList.add(ready.getUuid());
        }
        List<MemberNameResponse> nameList = authRepository.getNameListByUuid(uuidList);
        return new PostResponseForDetail(response, nameList);
    }

}
