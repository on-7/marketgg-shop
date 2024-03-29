package com.nhnacademy.marketgg.server.service.product;

import static com.nhnacademy.marketgg.server.constant.CouponsName.BESTREVIEW;
import static com.nhnacademy.marketgg.server.constant.PointContent.IMAGE_REVIEW;
import static com.nhnacademy.marketgg.server.constant.PointContent.NORMAL_REVIEW;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.info.MemberInfoRequest;
import com.nhnacademy.marketgg.server.dto.info.MemberInfoResponse;
import com.nhnacademy.marketgg.server.dto.request.review.ReviewCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.review.ReviewUpdateRequest;
import com.nhnacademy.marketgg.server.dto.response.file.ImageResponse;
import com.nhnacademy.marketgg.server.dto.response.product.ProductDetailResponse;
import com.nhnacademy.marketgg.server.dto.response.review.ReviewRatingResponse;
import com.nhnacademy.marketgg.server.dto.response.review.ReviewResponse;
import com.nhnacademy.marketgg.server.entity.Asset;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.Review;
import com.nhnacademy.marketgg.server.eventlistener.event.givencoupon.BestReviewedEvent;
import com.nhnacademy.marketgg.server.eventlistener.event.savepoint.NormalReviewedEvent;
import com.nhnacademy.marketgg.server.eventlistener.event.savepoint.PhotoReviewEvent;
import com.nhnacademy.marketgg.server.exception.asset.AssetNotFoundException;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.exception.review.ReviewNotFoundException;
import com.nhnacademy.marketgg.server.repository.asset.AssetRepository;
import com.nhnacademy.marketgg.server.repository.auth.AuthRepository;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.repository.product.ProductRepository;
import com.nhnacademy.marketgg.server.repository.review.ReviewRepository;
import com.nhnacademy.marketgg.server.service.file.FileService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * ReviewService의 구현체입니다.
 *
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class DefaultReviewService implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final AssetRepository assetRepository;
    private final ApplicationEventPublisher publisher;
    private final FileService fileService;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void createReview(final ReviewCreateRequest reviewRequest, final MultipartFile image,
                             final MemberInfo memberInfo) throws IOException {

        Member member =
                memberRepository.findById(memberInfo.getId()).orElseThrow(MemberNotFoundException::new);

        ImageResponse imageResponse = fileService.uploadImage(image);

        reviewRepository.save(new Review(reviewRequest, member, imageResponse.getAsset()));

        publisher.publishEvent(new PhotoReviewEvent(member, IMAGE_REVIEW.getContent()));
    }

    @Transactional
    @Override
    public void createReview(final ReviewCreateRequest reviewRequest, final MemberInfo memberInfo,
                             final Long productId) {
        Member member = memberRepository.findById(memberInfo.getId()).orElseThrow(MemberNotFoundException::new);

        ProductDetailResponse productDetailResponse = productRepository.queryById(productId);

        reviewRepository.save(new Review(reviewRequest, member, productDetailResponse.getAsset()));

        publisher.publishEvent(new NormalReviewedEvent(member, NORMAL_REVIEW.getContent()));
    }

    @Override
    public Page<ReviewResponse> retrieveReviews(final Pageable pageable, final Long productId)
            throws JsonProcessingException {
        Page<ReviewResponse> response = reviewRepository.retrieveReviews(pageable, productId);
        for (ReviewResponse reviewResponse : response.getContent()) {
            MemberInfoRequest memberInfoRequest = new MemberInfoRequest(reviewResponse.getUuid());
            ShopResult<MemberInfoResponse> memberInfo = authRepository.getMemberInfo(memberInfoRequest);
            reviewResponse.addMemberName(memberInfo.getData().getName());
        }

        return response;
    }

    @Override
    public List<ReviewRatingResponse> retrieveReviewsByRating(final Long productId) {
        List<ReviewRatingResponse> reviewRatingResponses = reviewRepository.retrieveReviewsByRating(productId);
        List<ReviewRatingResponse> list = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            int c = i;
            ReviewRatingResponse reviewRatingResponse =
                    reviewRatingResponses.stream().filter(o -> o.getRating() == c).findFirst().orElse(null);
            list.add(new ReviewRatingResponse((long) i, reviewRatingResponse == null ? 0l :
                    reviewRatingResponse.getRatingCount()));
        }
        return list;
    }


    @Override
    public ReviewResponse retrieveReviewDetails(final Long id) {
        return reviewRepository.queryById(id);
    }

    @Transactional
    @Override
    public void updateReview(final ReviewUpdateRequest reviewRequest, final Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(ReviewNotFoundException::new);
        Asset asset = assetRepository.findById(reviewRequest.getAssetId())
                                     .orElseThrow(AssetNotFoundException::new);

        review.updateReview(reviewRequest, asset);

        reviewRepository.save(review);

    }

    @Override
    public void deleteReview(final Long id) {
        reviewRepository.delete(reviewRepository.findById(id).orElseThrow(ReviewNotFoundException::new));
    }

    @Transactional
    @Override
    public Boolean makeBestReview(final Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(ReviewNotFoundException::new);
        review.makeBestReview();

        if (Boolean.TRUE.equals(review.getIsBest())) {
            reviewRepository.save(review);
            publisher.publishEvent(new BestReviewedEvent(BESTREVIEW.couponName(), review.getMember()));
            return true;
        }
        return false;
    }

    @Override
    public Page<ReviewResponse> retrieveReviewsByMember(final MemberInfo memberInfo, final Pageable pageable) {

        return reviewRepository.retrieveReviewsByMember(memberInfo, pageable);
    }

}
