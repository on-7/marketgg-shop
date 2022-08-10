package com.nhnacademy.marketgg.server.service.member;

import com.nhnacademy.marketgg.server.dto.request.member.MemberWithdrawRequest;
import com.nhnacademy.marketgg.server.dto.request.member.ShopMemberSignUpRequest;
import com.nhnacademy.marketgg.server.dto.response.member.MemberResponse;
import java.time.LocalDateTime;

/**
 * 회원 서비스입니다.
 *
 * @version 1.0.0
 */
public interface MemberService {

    /**
     * 지정한 회원을 GG 패스에 구독 처리합니다.
     *
     * @param id - GG 패스를 구독할 회원의 식별번호입니다.
     * @author 박세완
     * @since 1.0.0
     */
    void subscribePass(final Long id);

    /**
     * UUID 를 통해 사용자 정보를 요청합니다.
     *
     * @param uuid - 사용자를 식별할 수 있는 UUID
     * @return - 사용자 정보를 반환합니다.
     */
    MemberResponse retrieveMember(final String uuid);

    /**
     * 지정한 회원을 GG 패스에 구독해지 처리합니다.
     *
     * @param id - GG 패스를 구독해지할 회원의 식별번호입니다.
     * @author 박세완
     * @since 1.0.0
     */
    void withdrawPass(final Long id);

    /**
     * Client 에서 입력폼에 입력한 정보로 회원가입을 합니다.
     * (Auth 서버에 들어가는 정보랑 다름)
     *
     * @param shopMemberSignupRequest - 회원가입 정보를 담은 객체입니다.
     * @since 1.0.0
     */
    void signUp(final ShopMemberSignUpRequest shopMemberSignupRequest);

    void withdraw(final String uuid, final MemberWithdrawRequest memberWithdrawRequest);
}
