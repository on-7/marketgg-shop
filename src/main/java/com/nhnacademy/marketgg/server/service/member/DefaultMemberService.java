package com.nhnacademy.marketgg.server.service.member;

import com.nhnacademy.marketgg.server.dto.request.member.MemberWithdrawRequest;
import com.nhnacademy.marketgg.server.dto.request.member.ShopMemberSignUpRequest;
import com.nhnacademy.marketgg.server.dto.response.member.MemberResponse;
import com.nhnacademy.marketgg.server.entity.Cart;
import com.nhnacademy.marketgg.server.entity.DeliveryAddress;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.MemberGrade;
import com.nhnacademy.marketgg.server.entity.event.GivenCouponEvent;
import com.nhnacademy.marketgg.server.entity.event.SavePointEvent;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.exception.membergrade.MemberGradeNotFoundException;
import com.nhnacademy.marketgg.server.repository.cart.CartRepository;
import com.nhnacademy.marketgg.server.repository.deliveryaddress.DeliveryAddressRepository;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.repository.membergrade.MemberGradeRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultMemberService implements MemberService {

    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final MemberGradeRepository memberGradeRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    private final ApplicationEventPublisher publisher;

    @Override
    public LocalDateTime retrievePassUpdatedAt(final Long id) {
        Member member = memberRepository.findById(id)
                                        .orElseThrow(MemberNotFoundException::new);

        if (Objects.isNull(member.getGgpassUpdatedAt())) {
            return LocalDateTime.of(1, 1, 1, 1, 1, 1);
        }

        return member.getGgpassUpdatedAt();
    }

    @Transactional
    @Override
    public void subscribePass(final Long id) {
        Member member = memberRepository.findById(id)
                                        .orElseThrow(MemberNotFoundException::new);
        member.passSubscribe();

        // TODO : GG PASS 자동결제 로직 필요

        memberRepository.save(member);
    }

    @Override
    public MemberResponse retrieveMember(String uuid) {
        Member member = memberRepository.findByUuid(uuid)
                                        .orElseThrow(MemberNotFoundException::new);

        return MemberResponse.builder()
                             .memberGrade(member.getMemberGrade().getGrade())
                             .gender(member.getGender())
                             .birthDay(member.getBirthDate())
                             .ggpassUpdatedAt(member.getGgpassUpdatedAt())
                             .build();
    }

    @Transactional
    @Override
    public void withdrawPass(final Long id) {
        Member member = memberRepository.findById(id).orElseThrow(MemberNotFoundException::new);

        // TODO : GG PASS 자동결제 해지 로직 필요

        memberRepository.save(member);
    }

    /**
     * 회원가입시 회원정보를 DB 에 추가하는 메소드입니다.
     *
     * @param signUpRequest - 회원가입시 입력한 정보를 담고있는 객체입니다.
     * @return ShopMemberSignUpResponse - 회원가입을 하는 회원과 추천을 받게되는 회원의 uuid 를 담은 객체 입니다.
     */
    @Transactional
    @Override
    public void signUp(final ShopMemberSignUpRequest signUpRequest) {
        Cart savedCart = cartRepository.save(new Cart());

        Member signUpMember = memberRepository.save(new Member(signUpRequest, registerGrade(), savedCart));

        deliveryAddressRepository.save(new DeliveryAddress(signUpMember, signUpRequest));

        publisher.publishEvent(GivenCouponEvent.signUpCoupon(signUpMember));

        if (referrerCheck(signUpRequest) != null) {
            Member referredMember = memberRepository.findByUuid(referrerCheck(signUpRequest))
                                                    .orElseThrow(MemberNotFoundException::new);

            publisher.publishEvent(SavePointEvent.dispensePointForReferred(signUpMember));
            publisher.publishEvent(SavePointEvent.dispensePointForReferred(referredMember));
        }
    }

    /**
     * 추천인 여부를 체크하는 메소드 입니다.
     *
     * @param shopMemberSignUpRequest - 회원가입시 입력한 정보를 담고있는 객체입니다.
     * @return 추천인의 uuid 를 담고있는 메소드를 반환합니다.
     */
    private String referrerCheck(final ShopMemberSignUpRequest shopMemberSignUpRequest) {
        return shopMemberSignUpRequest.getReferrerUuid();
    }

    /**
     * 회원탈퇴시 SoftDelete 를 위한 메소드입니다.
     *
     * @param uuid                  - 탈퇴를 신청한 회원의 uuid 입니다.
     * @param memberWithdrawRequest - 탈퇴 신청 시간을 담은 객체입니다.
     */
    @Transactional
    @Override
    public void withdraw(final String uuid, final MemberWithdrawRequest memberWithdrawRequest) {
        Member member = memberRepository.findByUuid(uuid)
                                        .orElseThrow(MemberNotFoundException::new);
        member.withdraw(memberWithdrawRequest);
    }

    /**
     * 회원가입시 회원에게 부여되는 등급 추가 메소드 입니다.
     *
     * @return MemberGrade - 부여되는 등급 엔티티입니다.
     */
    private MemberGrade registerGrade() {
        return memberGradeRepository.findByGrade("Member")
                                    .orElseThrow(MemberGradeNotFoundException::new);
    }

}