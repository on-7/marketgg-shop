package com.nhnacademy.marketgg.server.repository.auth;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.server.dto.PageEntity;
import com.nhnacademy.marketgg.server.dto.ShopResult;
import com.nhnacademy.marketgg.server.dto.info.MemberInfoRequest;
import com.nhnacademy.marketgg.server.dto.info.MemberInfoResponse;
import com.nhnacademy.marketgg.server.dto.info.MemberNameResponse;
import com.nhnacademy.marketgg.server.dto.request.member.MemberUpdateRequest;
import com.nhnacademy.marketgg.server.dto.request.member.MemberWithdrawRequest;
import com.nhnacademy.marketgg.server.dto.request.member.SignupRequest;
import com.nhnacademy.marketgg.server.dto.response.auth.UuidTokenResponse;
import com.nhnacademy.marketgg.server.dto.response.member.AdminAuthResponse;
import com.nhnacademy.marketgg.server.dto.response.member.SignupResponse;
import com.nhnacademy.marketgg.server.exception.auth.AuthServerResponseException;
import com.nhnacademy.marketgg.server.exception.member.MemberInfoNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * auth 서버에서 uuid 목록을 전송해 이름목록을 가져옵니다.
 *
 * @author 박세완
 * @author 민아영
 * @author 김정민
 * @version 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthRepository {

    @Value("${gg.gateway.origin}")
    private String gateway;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private static final String DEFAULT_AUTH = "/auth/v1/members/info";
    private static final String DEFAULT_SIGNUP = "/auth/v1/members/signup";


    @Override
    public List<MemberNameResponse> getNameListByUuid(final List<String> uuidList) throws JsonProcessingException {

        if (uuidList.isEmpty()) {
            return Collections.emptyList();
        }

        String requestBody = objectMapper.writeValueAsString(uuidList);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, buildHeaders());
        ResponseEntity<ShopResult<List<MemberNameResponse>>> response = restTemplate.exchange(
            gateway + DEFAULT_AUTH + "/names",
            POST,
            requestEntity,
            new ParameterizedTypeReference<>() {
            });

        return Objects.requireNonNull(response.getBody()).getData();
    }

    @Override
    public ShopResult<MemberInfoResponse> getMemberInfo(final MemberInfoRequest memberInfoRequest)
        throws JsonProcessingException {
        String requestBody = objectMapper.writeValueAsString(memberInfoRequest);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, buildHeaders());
        ResponseEntity<ShopResult<MemberInfoResponse>> response = restTemplate.exchange(
            gateway + DEFAULT_AUTH + "/person",
            POST,
            requestEntity,
            new ParameterizedTypeReference<>() {
            });

        return response.getBody();
    }

    @Override
    public ShopResult<SignupResponse> signup(final SignupRequest signUpRequest) throws JsonProcessingException {
        String requestBody = objectMapper.writeValueAsString(signUpRequest);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, buildHeaders());
        ResponseEntity<ShopResult<SignupResponse>> response = restTemplate.exchange(
            gateway + DEFAULT_SIGNUP,
            POST,
            requestEntity,
            new ParameterizedTypeReference<>() {
            });

        return response.getBody();
    }

    @Override
    public void withdraw(final MemberWithdrawRequest memberWithdrawRequest, final String token) {
        HttpHeaders httpHeaders = buildHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, token);

        HttpEntity<MemberWithdrawRequest> requestEntity = new HttpEntity<>(memberWithdrawRequest, httpHeaders);

        restTemplate.exchange(
            gateway + DEFAULT_AUTH,
            DELETE,
            requestEntity,
            new ParameterizedTypeReference<>() {
            });
    }

    @Override
    public ShopResult<UuidTokenResponse> update(final MemberUpdateRequest memberUpdateRequest, final String token) {
        HttpHeaders httpHeaders = buildHeaders();
        httpHeaders.set(HttpHeaders.AUTHORIZATION, token);

        HttpEntity<MemberUpdateRequest> requestEntity = new HttpEntity<>(memberUpdateRequest, httpHeaders);
        ResponseEntity<ShopResult<UuidTokenResponse>> response = restTemplate.exchange(
            gateway + DEFAULT_AUTH,
            HttpMethod.PUT,
            requestEntity,
            new ParameterizedTypeReference<>() {
            });

        if (Objects.isNull(response.getBody())) {
            throw new AuthServerResponseException();
        }

        return response.getBody();
    }

    @Override
    public PageEntity<AdminAuthResponse> retrieveMemberList(final String jwt, final Pageable pageable) {

        String requestUrl = gateway + DEFAULT_AUTH + "/list?page=" + pageable.getPageNumber();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

        ResponseEntity<ShopResult<PageEntity<AdminAuthResponse>>> response = restTemplate.exchange(
            requestUrl, GET, httpEntity, new ParameterizedTypeReference<>() {
            });

        if (response.getStatusCode().is4xxClientError()) {
            throw new IllegalArgumentException("Client Exception!!");
        } else if (response.getStatusCode().is5xxServerError() || Objects.isNull(response.getBody())) {
            throw new IllegalArgumentException("Server Exception!!");
        }

        return Objects.requireNonNull(response.getBody()).getData();
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        return httpHeaders;
    }

    /**
     * 인증 서버로부터 회원 정보를 가져오는 정적 메서드입니다.
     */
    public static MemberInfoResponse checkResult(final ShopResult<MemberInfoResponse> shopResult) {
        if (!shopResult.isSuccess() || Objects.isNull(shopResult.getData())) {
            throw new MemberInfoNotFoundException(shopResult.getError().getMessage());
        }

        return shopResult.getData();
    }

}
