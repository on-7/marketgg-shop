package com.nhnacademy.marketgg.server.dto.request.cloud;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Auth {

    private String tenantId;
    private PasswordCredentials passwordCredentials;

}