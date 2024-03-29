package com.nhnacademy.marketgg.server.service.storage;

import static org.springframework.http.HttpMethod.PUT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.marketgg.server.dto.request.file.ImageCreateRequest;
import com.nhnacademy.marketgg.server.dto.request.file.cloud.Auth;
import com.nhnacademy.marketgg.server.dto.request.file.cloud.PasswordCredentials;
import com.nhnacademy.marketgg.server.dto.request.file.cloud.TokenRequest;
import com.nhnacademy.marketgg.server.dto.response.file.cloud.CloudResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Slf4j
public class NhnStorageService implements StorageService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gg.storage.auth-url}")
    private String authUrl;
    @Value("${gg.storage.user-name}")
    private String userName;
    @Value("${gg.storage.password}")
    private String password;
    @Value("${gg.storage.tenant-id}")
    private String tenantId;
    @Value("${gg.storage.storage-url}")
    private String storageUrl;

    private static final String HEADER_NAME = "X-Auth-Token";

    /**
     * NHN Cloud의 ObjectStorage를 사용하기 위한 메소드입니다.
     *
     * @return - Storage 이용에 필요한 토큰을 반환합니다.
     * @author - 조현진
     */
    private String requestToken() {

        PasswordCredentials passwordCredentials = new PasswordCredentials(userName, password);
        Auth auth = new Auth(tenantId, passwordCredentials);
        TokenRequest tokenRequest = new TokenRequest(auth);

        String identityUrl = this.authUrl + "/tokens";

        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpEntity<TokenRequest> httpEntity = new HttpEntity<>(tokenRequest, headers);

        // 토큰 요청
        ResponseEntity<String> response =
                this.restTemplate.exchange(identityUrl, HttpMethod.POST, httpEntity, String.class);

        return response.getBody();
    }

    @Override
    public ImageCreateRequest uploadImage(final MultipartFile image) throws IOException {

        String type = getContentType(image);
        String fileName = UUID.randomUUID() + type;
        String url = this.getUrl(fileName);

        RequestCallback requestCallback;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);

        RestTemplate storageRestTemplate = new RestTemplate(requestFactory);

        HttpMessageConverterExtractor<String> responseExtractor =
                new HttpMessageConverterExtractor<>(String.class, storageRestTemplate.getMessageConverters());

        try (InputStream inputStream = image.getInputStream()) {
            CloudResponse cloudResponse = objectMapper.readValue(requestToken(), CloudResponse.class);
            String tokenId = cloudResponse.getAccess().getToken().getId();

            requestCallback = request -> {
                request.getHeaders().add(HEADER_NAME, tokenId);
                IOUtils.copy(inputStream, request.getBody());
            };

            storageRestTemplate.execute(url, PUT, requestCallback, responseExtractor);
            log.info("업로드 성공");

        } catch (IOException e) {
            log.error("error: {}", e.getMessage());
            throw e;
        }

        return ImageCreateRequest.builder()
                                 .name(fileName)
                                 .imageAddress(url)
                                 .classification("cloud")
                                 .length(image.getSize())
                                 .type(type)
                                 .build();
    }

    private String getUrl(String fileName) {
        return this.storageUrl + "/on7_storage/" + fileName;
    }

    /**
     * 파일의 확장자를 관리하기 위한 메소드입니다.
     * 기본적으로 이미지타입이 아닐 경우 Exception을 발생시킵니다.
     *
     * @param image - MultipartFile 타입입니다.
     * @return - 확장자를 반환합니다.
     * @author - 조현진
     */
    private String getContentType(final MultipartFile image) {
        if (Objects.requireNonNull(image.getOriginalFilename()).contains("jpeg")) {
            return ".jpg";
        }
        if (Objects.requireNonNull(image.getOriginalFilename()).contains("jpg")) {
            return ".jpg";
        }
        if (Objects.requireNonNull(image.getOriginalFilename()).contains("png")) {
            return ".png";
        } else {
            throw new IllegalArgumentException("이미지만 업로드할 수 있습니다.");
        }
    }

}
