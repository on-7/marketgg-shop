package com.nhnacademy.marketgg.server.repository.cart;

import com.nhnacademy.marketgg.server.dto.request.order.ProductToOrder;
import com.nhnacademy.marketgg.server.dto.response.cart.CartProductResponse;
import com.nhnacademy.marketgg.server.entity.CartProduct;
import com.nhnacademy.marketgg.server.entity.QCartProduct;
import com.nhnacademy.marketgg.server.entity.QImage;
import com.querydsl.core.types.Projections;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.ArrayList;
import java.util.List;

public class CartProductRepositoryImpl extends QuerydslRepositorySupport implements CartProductRepositoryCustom {

    public CartProductRepositoryImpl() {
        super(CartProduct.class);
    }

    @Override
    public List<CartProductResponse> findCartProductsByCartId(final Long cartId) {
        QCartProduct cartProduct = QCartProduct.cartProduct;
        QImage image = QImage.image;

        return from(cartProduct)
                .innerJoin(cartProduct.product)
                .innerJoin(cartProduct.product.asset)
                .innerJoin(image).on(image.asset.id.eq(cartProduct.product.asset.id))
                .where(cartProduct.pk.cartId.eq(cartId))
                .select(Projections.constructor(CartProductResponse.class,
                                                cartProduct.product.id, image.imageAddress,
                                                cartProduct.product.name, cartProduct.amount,
                                                cartProduct.product.price))
                .fetch();
    }

    @Override
    public List<ProductToOrder> findCartProductsByProductIds(final Long cartId, final List<Long> productIds) {
        QCartProduct cartProduct = QCartProduct.cartProduct;
        QImage image = QImage.image;
        List<ProductToOrder> result = new ArrayList<>();

        for (Long id : productIds) {
            result.add(from(cartProduct)
                               .innerJoin(cartProduct.product)
                               .innerJoin(cartProduct.product.asset)
                               .innerJoin(image).on(image.asset.id.eq(cartProduct.product.asset.id))
                               .where(cartProduct.pk.cartId.eq(cartId))
                               .where(cartProduct.pk.productId.eq(id))
                               .select(Projections.constructor(ProductToOrder.class,
                                                               cartProduct.pk.productId,
                                                               cartProduct.product.name,
                                                               cartProduct.product.price,
                                                               cartProduct.amount,
                                                               image.imageAddress))
                               .fetchOne());
        }

        return result;
    }
}
