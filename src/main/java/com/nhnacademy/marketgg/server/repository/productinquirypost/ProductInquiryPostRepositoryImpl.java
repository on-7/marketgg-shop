package com.nhnacademy.marketgg.server.repository.productinquirypost;

import com.nhnacademy.marketgg.server.dto.response.product.ProductInquiryResponse;
import com.nhnacademy.marketgg.server.entity.ProductInquiryPost;
import com.nhnacademy.marketgg.server.entity.QProductInquiryPost;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

/**
 * 상품 문의 Repository 구현체 입니다.
 *
 * @author 민아영
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProductInquiryPostRepositoryImpl extends QuerydslRepositorySupport
    implements ProductInquiryPostRepositoryCustom {

    public ProductInquiryPostRepositoryImpl() {
        super(ProductInquiryPost.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param id       조회할 상품의 id 입니다.
     * @param pageable 요청하는 page 의 정보를 가지고 있습니다.
     * @return - 상품 문의 List 가 반환됩니다.
     * @author 민아영
     * @since 1.0.0
     */
    @Override
    public Page<ProductInquiryPost> findAllByProductNo(final Long id, final Pageable pageable) {
        QProductInquiryPost productInquiryPost = QProductInquiryPost.productInquiryPost;

        QueryResults<ProductInquiryPost> result = from(productInquiryPost)
            .where(productInquiryPost.product.id.eq(id))
            .orderBy(productInquiryPost.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * {@inheritDoc}
     *
     * @param id       조회할 회원의 id 입니다.
     * @param pageable 요청하는 page 의 정보를 가지고 있습니다.
     * @return 상품 문의 List 가 반환됩니다.
     * @author 민아영
     * @since 1.0.0
     */
    @Override
    public Page<ProductInquiryResponse> findAllByMemberNo(final Long id, final Pageable pageable) {
        QProductInquiryPost productInquiryPost = QProductInquiryPost.productInquiryPost;

        QueryResults<ProductInquiryResponse> result = from(productInquiryPost)
            .select(Projections.constructor(ProductInquiryResponse.class,
                                            productInquiryPost.member.uuid,
                                            productInquiryPost.product.id,
                                            productInquiryPost.productInquiryNo,
                                            productInquiryPost.product.name,
                                            productInquiryPost.title,
                                            productInquiryPost.content,
                                            productInquiryPost.isSecret,
                                            productInquiryPost.adminReply,
                                            productInquiryPost.createdDate))
            .where(productInquiryPost.member.id.eq(id))
            .orderBy(productInquiryPost.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    /**
     * {@inheritDoc}
     *
     * @param pageable 요청하는 page 의 정보를 가지고 있습니다.
     * @return 상품 문의 List 가 반환됩니다.
     * @author 민아영
     * @since 1.0.0
     */
    @Override
    public Page<ProductInquiryResponse> findAllByAdmin(final Pageable pageable) {
        QProductInquiryPost productInquiryPost = QProductInquiryPost.productInquiryPost;

        QueryResults<ProductInquiryResponse> result = from(productInquiryPost)
            .select(Projections.constructor(ProductInquiryResponse.class,
                                            productInquiryPost.member.uuid,
                                            productInquiryPost.product.id,
                                            productInquiryPost.productInquiryNo,
                                            productInquiryPost.product.name,
                                            productInquiryPost.title,
                                            productInquiryPost.content,
                                            productInquiryPost.isSecret,
                                            productInquiryPost.adminReply,
                                            productInquiryPost.createdDate))
            .orderBy(productInquiryPost.createdDate.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

}
