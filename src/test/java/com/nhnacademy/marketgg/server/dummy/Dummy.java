package com.nhnacademy.marketgg.server.dummy;

import com.nhnacademy.marketgg.server.dto.info.AuthInfo;
import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.dto.request.category.CategorizationCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.category.CategoryCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.customerservice.PostRequest;
import com.nhnacademy.marketgg.server.dto.request.customerservice.PostStatusUpdateRequest;
import com.nhnacademy.marketgg.server.dto.request.deliveryaddress.DeliveryAddressCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.deliveryaddress.DeliveryAddressUpdateRequest;
import com.nhnacademy.marketgg.server.dto.request.member.MemberCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.member.MemberGradeCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.order.OrderCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.order.ProductToOrder;
import com.nhnacademy.marketgg.server.dto.request.product.ProductCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.product.ProductToCartRequest;
import com.nhnacademy.marketgg.server.dto.request.product.ProductUpdateRequest;
import com.nhnacademy.marketgg.server.dto.response.customerservice.PostResponse;
import com.nhnacademy.marketgg.server.dto.response.deliveryaddress.DeliveryAddressResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderDetailRetrieveResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderFormResponse;
import com.nhnacademy.marketgg.server.dto.response.order.OrderGivenCoupon;
import com.nhnacademy.marketgg.server.dto.response.order.OrderRetrieveResponse;
import com.nhnacademy.marketgg.server.dto.response.product.ProductDetailResponse;
import com.nhnacademy.marketgg.server.dto.response.review.ReviewResponse;
import com.nhnacademy.marketgg.server.elastic.document.ElasticBoard;
import com.nhnacademy.marketgg.server.elastic.dto.request.SearchRequest;
import com.nhnacademy.marketgg.server.entity.Asset;
import com.nhnacademy.marketgg.server.entity.Cart;
import com.nhnacademy.marketgg.server.entity.CartProduct;
import com.nhnacademy.marketgg.server.entity.Categorization;
import com.nhnacademy.marketgg.server.entity.Category;
import com.nhnacademy.marketgg.server.entity.CustomerServicePost;
import com.nhnacademy.marketgg.server.entity.DeliveryAddress;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.MemberGrade;
import com.nhnacademy.marketgg.server.entity.Order;
import com.nhnacademy.marketgg.server.entity.OrderProduct;
import com.nhnacademy.marketgg.server.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Dummy {

    public static String DUMMY_UUID = "022db29c-d0e2-11e5-bb4c-60f81dca7676";

    public static MemberCreateRequest getDummyMemberCreateRequest(String uuid) {
        MemberGradeCreateRequest memberGradeCreateRequest = new MemberGradeCreateRequest();
        ReflectionTestUtils.setField(memberGradeCreateRequest, "grade", "memberGrade");
        MemberGrade memberGrade = new MemberGrade(memberGradeCreateRequest);

        MemberCreateRequest memberCreateRequest = new MemberCreateRequest();
        ReflectionTestUtils.setField(memberCreateRequest, "memberGrade", memberGrade);
        ReflectionTestUtils.setField(memberCreateRequest, "uuid", uuid);
        ReflectionTestUtils.setField(memberCreateRequest, "gender", 'M');

        LocalDate birthDate = LocalDate.of(1997, 4, 6);
        ReflectionTestUtils.setField(memberCreateRequest, "birthDate", birthDate);

        LocalDateTime now = LocalDateTime.now().withNano(0);
        ReflectionTestUtils.setField(memberCreateRequest, "ggpassUpdateAt", now);

        ReflectionTestUtils.setField(memberCreateRequest, "createdAt", now);
        ReflectionTestUtils.setField(memberCreateRequest, "updatedAt", now);

        return memberCreateRequest;
    }

    public static Member getDummyMember(Cart cart) {
        return new Member(getDummyMemberCreateRequest(DUMMY_UUID), cart);
    }

    public static Member getDummyMember(Cart cart, MemberGrade memberGrade) {
        return new Member(getDummyMemberCreateRequest(DUMMY_UUID), memberGrade, cart);
    }

    public static Member getDummyMember(String uuid, Cart cart) {
        return new Member(getDummyMemberCreateRequest(uuid), cart);
    }

    public static ProductCreateRequest getDummyProductCreateRequest() {
        ProductCreateRequest productRequest = new ProductCreateRequest();
        ReflectionTestUtils.setField(productRequest, "categoryCode", "001");
        ReflectionTestUtils.setField(productRequest, "name", "자몽");
        ReflectionTestUtils.setField(productRequest, "content", "아침에 자몽 쥬스");
        ReflectionTestUtils.setField(productRequest, "totalStock", 100L);
        ReflectionTestUtils.setField(productRequest, "price", 2000L);
        ReflectionTestUtils.setField(productRequest, "description", "자몽주스 설명");
        ReflectionTestUtils.setField(productRequest, "unit", "1박스");
        ReflectionTestUtils.setField(productRequest, "deliveryType", "샛별배송");
        ReflectionTestUtils.setField(productRequest, "origin", "인도네시아");
        ReflectionTestUtils.setField(productRequest, "packageType", "냉장");
        ReflectionTestUtils.setField(productRequest, "allergyInfo", "새우알러지");
        ReflectionTestUtils.setField(productRequest, "expirationDate", LocalDate.now());
        ReflectionTestUtils.setField(productRequest, "capacity", "20개");
        ReflectionTestUtils.setField(productRequest, "labelNo", 1L);

        return productRequest;
    }

    public static ProductUpdateRequest getDummyProductUpdateRequest() {
        ProductUpdateRequest productRequest = new ProductUpdateRequest();
        ReflectionTestUtils.setField(productRequest, "categoryCode", "001");
        ReflectionTestUtils.setField(productRequest, "name", "자몽");
        ReflectionTestUtils.setField(productRequest, "content", "아침에 자몽 쥬스");
        ReflectionTestUtils.setField(productRequest, "totalStock", 100L);
        ReflectionTestUtils.setField(productRequest, "price", 2000L);
        ReflectionTestUtils.setField(productRequest, "description", "자몽주스 설명");
        ReflectionTestUtils.setField(productRequest, "unit", "1박스");
        ReflectionTestUtils.setField(productRequest, "deliveryType", "샛별배송");
        ReflectionTestUtils.setField(productRequest, "origin", "인도네시아");
        ReflectionTestUtils.setField(productRequest, "packageType", "냉장");
        ReflectionTestUtils.setField(productRequest, "allergyInfo", "새우알러지");
        ReflectionTestUtils.setField(productRequest, "expirationDate", LocalDate.now());
        ReflectionTestUtils.setField(productRequest, "capacity", "20개");
        ReflectionTestUtils.setField(productRequest, "labelNo", 1L);
        ReflectionTestUtils.setField(productRequest, "assetNo", 1L);

        return productRequest;
    }

    public static ProductDetailResponse getDummyProductResponse() {

        return new ProductDetailResponse(1L, Asset.create(), 1L, "001", "채소", "자몽",
                                         "아침에 자몽 쥬스", 100L, 2000L, "자몽쥬스 설명",
                                         "1박스", "샛별 배송", "인도네시아", "냉장", LocalDate.now(),
                                         "새우알러지", "20개", LocalDateTime.now(), LocalDateTime.now(), null);
    }

    public static Asset getDummyAsset(Long id) {
        Asset asset = Asset.create();
        ReflectionTestUtils.setField(asset, "id", id);
        return asset;
    }

    public static Asset getDummyAsset() {
        return Asset.create();
    }

    public static CategorizationCreateRequest getDummyCategorizationCreateRequest() {
        CategorizationCreateRequest categorizationRequest = new CategorizationCreateRequest();

        ReflectionTestUtils.setField(categorizationRequest, "categorizationCode", "100");
        ReflectionTestUtils.setField(categorizationRequest, "name", "상품");
        ReflectionTestUtils.setField(categorizationRequest, "alias", "Products");

        return categorizationRequest;
    }

    public static Categorization getDummyCategorization() {
        return new Categorization(getDummyCategorizationCreateRequest());
    }

    public static Category getDummyCategory() {
        CategoryCreateRequest categoryRequest = new CategoryCreateRequest();
        ReflectionTestUtils.setField(categoryRequest, "categoryCode", "001");
        ReflectionTestUtils.setField(categoryRequest, "categorizationCode", "100");
        ReflectionTestUtils.setField(categoryRequest, "name", "채소");
        ReflectionTestUtils.setField(categoryRequest, "sequence", 1);

        return new Category(categoryRequest, getDummyCategorization());
    }

    public static Product getDummyProduct(Long assetId) {
        return new Product(getDummyProductCreateRequest(), getDummyAsset(assetId), getDummyCategory());
    }

    public static Product getDummyProduct(Long productId, Long assetId) {
        Product product =
            new Product(getDummyProductCreateRequest(), getDummyAsset(assetId), getDummyCategory());
        ReflectionTestUtils.setField(product, "id", productId);

        return product;
    }

    public static ProductToCartRequest getDummyProductToCartRequest(Long productId) {
        ProductToCartRequest request = new ProductToCartRequest();
        ReflectionTestUtils.setField(request, "id", productId);
        ReflectionTestUtils.setField(request, "amount", 3);

        return request;
    }

    public static ProductToCartRequest getDummyProductToCartRequest(Long productId, Integer amount) {
        ProductToCartRequest request = new ProductToCartRequest();
        ReflectionTestUtils.setField(request, "id", productId);
        ReflectionTestUtils.setField(request, "amount", amount);

        return request;
    }

    public static MemberInfo getDummyMemberInfo(Long id, Cart cart) {
        LocalDate birthDate = LocalDate.of(1997, 4, 6);
        LocalDateTime ggpassUpdatedAt = LocalDateTime.now();
        return new MemberInfo(id, cart, null, 'M', birthDate);
    }

    public static AuthInfo getDummyAuthInfo() {
        AuthInfo authInfo = new AuthInfo();
        ReflectionTestUtils.setField(authInfo, "email", "dummy@dooray.com");
        ReflectionTestUtils.setField(authInfo, "name", "김더미");
        ReflectionTestUtils.setField(authInfo, "phoneNumber", "010-1111-1111");
        return authInfo;
    }

    public static Cart getDummyCart(Long id) {
        Cart cart = new Cart();
        ReflectionTestUtils.setField(cart, "id", id);
        return cart;
    }

    public static CartProduct getCartProduct(Long cartId, Long productId, Integer amount) {
        return new CartProduct(getDummyCart(cartId), getDummyProduct(productId), amount);
    }

    public static PostRequest getPostRequest() {
        PostRequest postRequest = new PostRequest();
        ReflectionTestUtils.setField(postRequest, "categoryCode", "702");
        ReflectionTestUtils.setField(postRequest, "title", "title");
        ReflectionTestUtils.setField(postRequest, "content", "content");
        ReflectionTestUtils.setField(postRequest, "reason", "상품");

        return postRequest;
    }

    public static PostStatusUpdateRequest getPostStatusUpdateRequest() {
        PostStatusUpdateRequest updateRequest = new PostStatusUpdateRequest();
        ReflectionTestUtils.setField(updateRequest, "status", "답변중");

        return updateRequest;
    }

    public static CustomerServicePost getCustomerServicePost() {
        CustomerServicePost post = new CustomerServicePost(getDummyMember(new Cart()), getDummyCategory(),
                                                           getPostRequest());
        ReflectionTestUtils.setField(post, "id", 1L);

        return post;
    }

    public static ElasticBoard getElasticBoard() {
        return new ElasticBoard(getCustomerServicePost());
    }

    public static PostResponse getDummyPostResponse() {
        return new PostResponse(1L, "701", "title", "상품", "empty", LocalDateTime.now());
    }

    public static SearchRequest getSearchRequest() {
        return new SearchRequest();
    }

    public static DeliveryAddressCreateRequest getDeliveryAddressCreateRequest() {
        DeliveryAddressCreateRequest createRequest = new DeliveryAddressCreateRequest();

        ReflectionTestUtils.setField(createRequest, "isDefaultAddress", false);
        ReflectionTestUtils.setField(createRequest, "zipCode", 50182);
        ReflectionTestUtils.setField(createRequest, "address", "김해시 내동 삼성아파트");
        ReflectionTestUtils.setField(createRequest, "detailAddress", "3층");

        return createRequest;
    }

    public static DeliveryAddressUpdateRequest getDeliveryAddressUpdateRequest() {
        DeliveryAddressUpdateRequest updateRequest = new DeliveryAddressUpdateRequest();

        ReflectionTestUtils.setField(updateRequest, "id", 1L);
        ReflectionTestUtils.setField(updateRequest, "isDefaultAddress", true);
        ReflectionTestUtils.setField(updateRequest, "zipCode", 50211);
        ReflectionTestUtils.setField(updateRequest, "address", "이젠 부산시");
        ReflectionTestUtils.setField(updateRequest, "detailAddress", "온천장 근처 허심청");

        return updateRequest;
    }

    public static DeliveryAddress getDeliveryAddress() {
        return new DeliveryAddress(getDummyMember(getDummyCart(1L)), getDeliveryAddressCreateRequest());
    }

    public static DeliveryAddressResponse getDummyDeliveryAddressResponse() {
        return new DeliveryAddressResponse(1L, true, 50948,
                                           "경남 김해시 내외중앙로 55", "정우빌딩 5층");
    }

    public static OrderFormResponse getDummyOrderFormResponse() {
        MemberInfo memberInfo = getDummyMemberInfo(1L, new Cart());
        AuthInfo authInfo = getDummyAuthInfo();
        return new OrderFormResponse(List.of(), memberInfo.getId(), authInfo.getName(), authInfo.getEmail(),
                                     memberInfo.getMemberGrade(), true, List.of(), 10000,
                                     List.of(), List.of(), 50_000L);
    }

    public static OrderDetailRetrieveResponse getDummyOrderDetailResponse() {
        return new OrderDetailRetrieveResponse(1L, 1L, 50_000L, "결제대기",
                                               1000, null, 12345, "주소",
                                               "상세주소", LocalDateTime.now());

    }

    public static DeliveryAddress getDummyDeliveryAddress() {
        DeliveryAddressCreateRequest request = new DeliveryAddressCreateRequest();
        ReflectionTestUtils.setField(request, "isDefaultAddress", true);
        ReflectionTestUtils.setField(request, "zipCode", 50948);
        ReflectionTestUtils.setField(request, "address", "경남 김해시 내외중앙로 55");
        ReflectionTestUtils.setField(request, "detailAddress", "정우빌딩 5층");

        return new DeliveryAddress(Dummy.getDummyMember(Dummy.getDummyCart(1L)), request);
    }

    // public static Coupon getDummyCoupon() {
    //     CouponDto couponDto = new CouponDto();
    //     ReflectionTestUtils.setField(couponDto, "id", 1L);
    //     ReflectionTestUtils.setField(couponDto, "name", "2022 썸머 이벤트");
    //     ReflectionTestUtils.setField(couponDto, "type", "정액할인");
    //     ReflectionTestUtils.setField(couponDto, "expiredDate", 30);
    //     ReflectionTestUtils.setField(couponDto, "minimumMoney", 10000);
    //     ReflectionTestUtils.setField(couponDto, "discountAmount", 1000);
    //
    //     return new Coupon();
    // }
    public static Coupon getDummyCoupon() {
        CouponDto couponDto = new CouponDto(1L, "2022 썸머 이벤트", "정액할인", 30, 10000, 1000D);
        // ReflectionTestUtils.setField(couponDto, "id", 1L);
        // ReflectionTestUtils.setField(couponDto, "name", "2022 썸머 이벤트");
        // ReflectionTestUtils.setField(couponDto, "type", "정액할인");
        // ReflectionTestUtils.setField(couponDto, "expiredDate", 30);
        // ReflectionTestUtils.setField(couponDto, "minimumMoney", 10000);
        // ReflectionTestUtils.setField(couponDto, "discountAmount", 1000D);

        return new Coupon(couponDto);
    }

<<<<<<< HEAD
    public static ProductToOrder getDummyProductToOrder() {
        ProductToOrder productToOrder = new ProductToOrder();

        ReflectionTestUtils.setField(productToOrder, "id", 1L);
        ReflectionTestUtils.setField(productToOrder, "name", "상품1");
        ReflectionTestUtils.setField(productToOrder, "price", 10000L);
        ReflectionTestUtils.setField(productToOrder, "amount", 1);

        return productToOrder;
    }

    public static OrderCreateRequest getDummyOrderCreateRequest() {
        List<ProductToOrder> productToOrders = List.of(getDummyProductToOrder());
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest();

        ReflectionTestUtils.setField(orderCreateRequest, "name", "KimDummy");
        ReflectionTestUtils.setField(orderCreateRequest, "email", "KimDummy@dooray.com");
        ReflectionTestUtils.setField(orderCreateRequest, "couponId", 1L);
        ReflectionTestUtils.setField(orderCreateRequest, "deliveryAddressId", 1L);
        ReflectionTestUtils.setField(orderCreateRequest, "products", productToOrders);
        ReflectionTestUtils.setField(orderCreateRequest, "usedPoint", 1000);
        ReflectionTestUtils.setField(orderCreateRequest, "totalOrigin", 10000L);
        ReflectionTestUtils.setField(orderCreateRequest, "totalAmount", 9000L);
        ReflectionTestUtils.setField(orderCreateRequest, "paymentType", "카드");
        ReflectionTestUtils.setField(orderCreateRequest, "expectedSavePoint", 90);

        return orderCreateRequest;
    }

    public static Order getDummyOrder() {
        return new Order(getDummyMember(getDummyCart(1L)), getDummyDeliveryAddress(), getDummyOrderCreateRequest());
    }

    public static OrderProduct getDummyOrderProduct() {
        return new OrderProduct(getDummyOrder(), getDummyProduct(1L), 1);
    }

    public static OrderGivenCoupon getDummyOrderGivenCoupon() {
        Coupon coupon = getDummyCoupon();

        return new OrderGivenCoupon(coupon.getId(), coupon.getName(), LocalDateTime.now(), 30,
                                    5000, 1000D);
    }

    public static OrderRetrieveResponse getOrderRetrieveResponse() {
        return new OrderRetrieveResponse(1L, 1L, 10000L,
                                         "결제 대기", LocalDateTime.now());
    }

    public static ReviewResponse getDummyReviewResponse() {

        return new ReviewResponse(1L, 1L, 1L, "후기 내용입니다. 더미입니다.", 5L,
                                  Boolean.FALSE, LocalDateTime.now(), LocalDateTime.now(), null);
    }

}
