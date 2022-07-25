package com.nhnacademy.marketgg.server.service.impl;

import com.nhnacademy.marketgg.server.constant.CustomerServicePostStatus;
import com.nhnacademy.marketgg.server.dto.response.CustomerServicePostDto;
import com.nhnacademy.marketgg.server.entity.Category;
import com.nhnacademy.marketgg.server.entity.CustomerServicePost;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.exception.category.CategoryNotFoundException;
import com.nhnacademy.marketgg.server.exception.customerservicepost.CustomerServicePostNotFoundException;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.mapper.impl.CustomerServicePostMapper;
import com.nhnacademy.marketgg.server.repository.category.CategoryRepository;
import com.nhnacademy.marketgg.server.repository.customerservicepost.CustomerServicePostRepository;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.service.CustomerServicePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultCustomerServicePostService implements CustomerServicePostService {

    private final CustomerServicePostMapper customerServicePostMapper;
    private final CustomerServicePostRepository customerServicePostRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    private static String OTO_INQUIRY = "1:1문의";

    @Transactional
    @Override
    public void createOtoInquiry(Long memberId, CustomerServicePostDto customerServicePostDto) {
        CustomerServicePost customerServicePost = customerServicePostMapper.toEntity(customerServicePostDto);
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(MemberNotFoundException::new);
        String categoryId = categoryRepository.retrieveCategoryIdByName(OTO_INQUIRY)
                                              .orElseThrow(CategoryNotFoundException::new);
        Category category = categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        customerServicePost.setMember(member);
        customerServicePost.setCategory(category);
        customerServicePost.setStatus(CustomerServicePostStatus.UNANSWERED.status());
        customerServicePost.setCreatedAt(LocalDateTime.now());
        customerServicePost.setUpdatedAt(LocalDateTime.now());

        customerServicePostRepository.save(customerServicePost);
    }

    @Override
    public CustomerServicePostDto retrieveOtoInquiry(Long inquiryId) {
        CustomerServicePost otoInquiry = customerServicePostRepository.findById(inquiryId)
                                                                      .orElseThrow(
                                                                              CustomerServicePostNotFoundException::new);

        return customerServicePostMapper.toDto(otoInquiry);
    }

    @Override
    public List<CustomerServicePostDto> retrieveOtoInquiries(final Pageable pageable) {
        String categoryId = categoryRepository.retrieveCategoryIdByName(OTO_INQUIRY).orElseThrow(
                CategoryNotFoundException::new);
        List<CustomerServicePost> otoInquiries = customerServicePostRepository.findAllOtoInquiries(pageable, categoryId)
                                                                              .getContent();

        return otoInquiries.stream()
                           .map(customerServicePostMapper::toDto)
                           .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    @Override
    public void deleteOtoInquiry(Long inquiryId) {
        CustomerServicePost otoInquiry = customerServicePostRepository.findById(inquiryId).orElseThrow(
                CustomerServicePostNotFoundException::new);

        customerServicePostRepository.delete(otoInquiry);
    }

    @Override
    public List<CustomerServicePostDto> retrieveOwnOtoInquiries(Pageable pageable, Long memberId) {
        String categoryId = categoryRepository.retrieveCategoryIdByName(OTO_INQUIRY)
                                              .orElseThrow(CategoryNotFoundException::new);

        List<CustomerServicePost> ownOtoInquiries = customerServicePostRepository.findAllOwnOtoInquiries(pageable,
                                                                                                         categoryId,
                                                                                                         memberId)
                                                                                 .getContent();

        return ownOtoInquiries.stream()
                              .map(customerServicePostMapper::toDto)
                              .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public CustomerServicePostDto retrieveOwnOtoInquiry(Long inquiryId, Long memberId) {
        CustomerServicePost ownOtoInquiry = customerServicePostRepository.findOwnOtoInquiry(inquiryId, memberId);

        return customerServicePostMapper.toDto(ownOtoInquiry);
    }

    @Transactional
    @Override
    public void deleteOwnOtoInquiry(Long inquiryId, Long memberId) {
        CustomerServicePost ownOtoInquiry = customerServicePostRepository.findOwnOtoInquiry(inquiryId, memberId);

        customerServicePostRepository.delete(ownOtoInquiry);
    }
}
