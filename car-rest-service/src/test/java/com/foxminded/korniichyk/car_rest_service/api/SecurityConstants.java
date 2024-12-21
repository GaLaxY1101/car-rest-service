package com.foxminded.korniichyk.car_rest_service.api;

import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityConstants {


    public final static Jwt MOCKED_JWT = Jwt.withTokenValue("valid-auth-token")
            .header("alg", "RS256")
            .claim("sub", "valid")
            .claim("email", "valid@gmail.com")
            .claim("roles", "ROLE_USER")
            .build();

    public final static HttpHeaders AUTH_HEADER = new HttpHeaders() {
        {
            add("Authorization", "Bearer " + MOCKED_JWT.getTokenValue());
        }
    };
}
