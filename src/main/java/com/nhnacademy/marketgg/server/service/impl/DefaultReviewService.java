package com.nhnacademy.marketgg.server.service.impl;

import com.nhnacademy.marketgg.server.dto.request.ReviewCreateRequest;
import com.nhnacademy.marketgg.server.dto.response.ReviewResponse;
import com.nhnacademy.marketgg.server.dto.response.common.SingleResponse;
import com.nhnacademy.marketgg.server.entity.Asset;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.Review;
import com.nhnacademy.marketgg.server.exception.asset.AssetNotFoundException;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.repository.asset.AssetRepository;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.repository.review.ReviewRepository;
import com.nhnacademy.marketgg.server.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReviewService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final AssetRepository assetRepository;

    @Override
    public void createReview(final ReviewCreateRequest reviewRequest, final String uuid) {
        Member member = this.memberRepository.findByUuid(uuid).orElseThrow(MemberNotFoundException::new);

        Asset asset = this.assetRepository.findById(reviewRequest.getAssetNo())
                                          .orElseThrow(AssetNotFoundException::new);

        this.reviewRepository.save(new Review(reviewRequest, member, asset));
    }

    @Override
    public SingleResponse<Page> retrieveReviews(final Pageable pageable) {
        Page<ReviewResponse> response = this. reviewRepository.retrieveReviews(pageable);
        SingleResponse<Page> reviews = new SingleResponse<>(response);

        return reviews;
    }
}