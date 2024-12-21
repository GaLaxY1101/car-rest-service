package com.foxminded.korniichyk.car_rest_service.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.RegisterRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.AuthenticationFailedException;
import com.foxminded.korniichyk.car_rest_service.exception.FailToCheckUserExistenceException;
import com.foxminded.korniichyk.car_rest_service.exception.FailToGetAdminTokenException;
import com.foxminded.korniichyk.car_rest_service.exception.JsonCustomException;
import com.foxminded.korniichyk.car_rest_service.exception.RegistrationFailedException;
import com.foxminded.korniichyk.car_rest_service.exception.UserWithProvidedEmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Auth0Service {


    @Value("${auth0.client-id}")
    private String clientId;

    @Value("${auth0.client-secret}")
    private String clientSecret;

    @Value("${auth0.api-audience}")
    private String audience;

    @Value("${auth0.issuer}")
    private String issuer;


    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final String AUTH0_MANAGEMENT_API_PATH = "/api/v2/users";

    private final String AUTH0_TOKEN_API_PATH = "/oauth/token";


    public String register(RegisterRequestDto registerRequestDto) {

        String url = issuer + AUTH0_MANAGEMENT_API_PATH;
        String payload = createRegisterPayload(registerRequestDto);

        if(isUserExistByEmail(registerRequestDto.getEmail())) {
            throw new UserWithProvidedEmailAlreadyExistsException("User with email " + registerRequestDto.getEmail() + " already exists");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAdminApiToken());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return "User created successfully";
        } else {
            throw new RegistrationFailedException(response.getBody());
        }
    }

    private boolean isUserExistByEmail(String email) {

        String url = issuer + "/api/v2/users-by-email?email=" + email;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAdminApiToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<?> users = mapper.readValue(response.getBody(), List.class);
                return !users.isEmpty();
            } catch (JsonProcessingException e) {
                throw new JsonCustomException("Error parsing user existence response: " + e.getMessage());
            }
        } else {
            throw new FailToCheckUserExistenceException(response.getBody());
        }

    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationRequestDto) {
        String url = issuer + AUTH0_TOKEN_API_PATH;

        String payload = createAuthPayload(authenticationRequestDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        try{
            if (response.getStatusCode().is2xxSuccessful()) {

                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                String token = jsonResponse.get("access_token").asText();
                return new AuthenticationResponseDto(token);
            } else {
                throw new AuthenticationFailedException("Failed to authenticate user: " + response.getBody());
            }
        } catch (JsonProcessingException ex) {
            throw new JsonCustomException(ex.getMessage());
        }

    }

    private String getAdminApiToken() {

        String url = issuer + AUTH0_TOKEN_API_PATH;

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("grant_type", "client_credentials");
        payloadMap.put("client_id", clientId);
        payloadMap.put("client_secret", clientSecret);
        payloadMap.put("audience", "https://dev-0hzkov1ez5d8znzj.us.auth0.com/api/v2/");

        try {
            String payload = objectMapper.writeValueAsString(payloadMap);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);


            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();

            if (responseBody == null) {
                throw new FailToGetAdminTokenException("Failed to retrieve Admin API token");
            }

            JsonNode jsonResponse = objectMapper.readTree(responseBody);
            return jsonResponse.get("access_token").asText();
        } catch (JsonProcessingException ex) {
            throw new JsonCustomException(ex.getMessage());
        }

    }


    private String createAuthPayload(AuthenticationRequestDto authenticationRequestDto) {
        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("grant_type", "password");
        payloadMap.put("client_id", clientId);
        payloadMap.put("client_secret", clientSecret);
        payloadMap.put("username", authenticationRequestDto.getEmail());
        payloadMap.put("password", authenticationRequestDto.getPassword());
        payloadMap.put("audience", audience);
        payloadMap.put("connection", "Username-Password-Authentication");

        try {
            return objectMapper.writeValueAsString(payloadMap);
        } catch (JsonProcessingException ex) {
            throw new JsonCustomException(ex.getMessage());
        }
    }

    private String createRegisterPayload(RegisterRequestDto registerRequestDto) {

        Map<String, String> payload = new HashMap<>();
        payload.put("email", registerRequestDto.getEmail());
        payload.put("password", registerRequestDto.getPassword());
        payload.put("connection", "Username-Password-Authentication");

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new JsonCustomException(ex.getMessage());
        }
    }
}
