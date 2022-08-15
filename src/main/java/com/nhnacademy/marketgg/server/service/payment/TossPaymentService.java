package com.nhnacademy.marketgg.server.service.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.server.dto.payment.PaymentResponse;
import com.nhnacademy.marketgg.server.dto.payment.request.PaymentCancelRequest;
import com.nhnacademy.marketgg.server.dto.payment.request.PaymentRequest;
import com.nhnacademy.marketgg.server.dto.payment.request.PaymentVerifyRequest;
import com.nhnacademy.marketgg.server.entity.payment.CardPayment;
import com.nhnacademy.marketgg.server.entity.payment.MobilePhonePayment;
import com.nhnacademy.marketgg.server.entity.payment.Payment;
import com.nhnacademy.marketgg.server.entity.payment.TransferPayment;
import com.nhnacademy.marketgg.server.entity.payment.VirtualAccountPayment;
import com.nhnacademy.marketgg.server.repository.order.OrderRepository;
import com.nhnacademy.marketgg.server.repository.payment.CardPaymentRepository;
import com.nhnacademy.marketgg.server.repository.payment.MobilePhonePaymentRepository;
import com.nhnacademy.marketgg.server.repository.payment.PaymentAdapter;
import com.nhnacademy.marketgg.server.repository.payment.PaymentRepository;
import com.nhnacademy.marketgg.server.repository.payment.TransferPaymentRepository;
import com.nhnacademy.marketgg.server.repository.payment.VirtualAccountPaymentRepository;
import java.io.UncheckedIOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TossPaymentService implements PaymentService {

    private final PaymentAdapter paymentAdapter;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CardPaymentRepository cardPaymentRepository;
    private final VirtualAccountPaymentRepository virtualAccountPaymentRepository;
    private final TransferPaymentRepository transferPaymentRepository;
    private final MobilePhonePaymentRepository mobilePhonePaymentRepository;

    @Override
    public PaymentResponse verifyRequest(final PaymentVerifyRequest paymentRequest) {
        return new PaymentResponse();
    }

    /**
     * 최종 결제 승인을 처리합니다.
     *
     * @param paymentRequest - 결제 요청 정보
     * @return PaymentResponse
     */
    @Override
    public PaymentResponse pay(final PaymentRequest paymentRequest) {
        ResponseEntity<String> response = paymentAdapter.confirm(paymentRequest);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        PaymentResponse paymentResponse;
        try {
            paymentResponse = objectMapper.readValue(response.getBody(), PaymentResponse.class);
        } catch (JsonProcessingException ex) {
            throw new UncheckedIOException(ex);
        }

        Payment payment = this.toEntity(paymentResponse);
        Payment savedPayment = paymentRepository.save(payment);

        if (Objects.nonNull(paymentResponse.getCard())) {
            CardPayment cardPayment = this.toEntity(paymentResponse.getCard());
            cardPaymentRepository.save(cardPayment);
        }

        if (Objects.nonNull(paymentResponse.getVirtualAccount())) {
            VirtualAccountPayment virtualAccountPayment = this.toEntity(paymentResponse.getVirtualAccount());
            virtualAccountPaymentRepository.save(virtualAccountPayment);
        }

        if (Objects.nonNull(paymentResponse.getTransfer())) {
            TransferPayment transferPayment = this.toEntity(paymentResponse.getTransfer());
            transferPaymentRepository.save(transferPayment);
        }

        if (Objects.nonNull(paymentResponse.getMobilePhone())) {
            MobilePhonePayment mobilePhonePayment = this.toEntity(paymentResponse.getMobilePhone());
            mobilePhonePaymentRepository.save(mobilePhonePayment);
        }

        return null;
    }

    @Override
    public PaymentResponse cancelPayment(Long paymentKey, PaymentCancelRequest paymentRequest) {
        return null;
    }

}