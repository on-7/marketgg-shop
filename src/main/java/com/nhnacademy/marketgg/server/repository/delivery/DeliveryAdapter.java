package com.nhnacademy.marketgg.server.repository.delivery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.server.dto.request.order.OrderInfoRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeliveryAdapter implements DeliveryRepository {

    @Value("${gg.delivery.origin}")
    private String defaultEggplantDelivery;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Override
    public ResponseEntity<Void> createTrackingNo(final OrderInfoRequestDto orderInfoRequestDto)
            throws JsonProcessingException {
        String requestBody = objectMapper.writeValueAsString(orderInfoRequestDto);
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, buildHeaders());
        return restTemplate.exchange(
                 defaultEggplantDelivery + "/eggplant-delivery/tracking-no",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {
                });
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));

        return httpHeaders;
    }

}
