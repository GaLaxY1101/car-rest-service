package com.foxminded.korniichyk.car_rest_service.api;

import com.foxminded.korniichyk.car_rest_service.configuration.TestContainersConfig;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.EngineNotFoundException;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import com.foxminded.korniichyk.car_rest_service.security.Auth0Service;
import com.foxminded.korniichyk.car_rest_service.service.impl.EngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import static com.foxminded.korniichyk.car_rest_service.api.SecurityConstants.AUTH_HEADER;
import static com.foxminded.korniichyk.car_rest_service.api.SecurityConstants.MOCKED_JWT;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createEngine;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createEngineCreateRequestDto;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createEngineUpdateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = TestContainersConfig.class)
@Sql(scripts = {"/db/scripts/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/db/scripts/initData.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EngineControllerTest {

    @Autowired
    private EngineService engineService;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private Auth0Service auth0Service;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final String BASE_URL = "/api/v1/engines";


    @BeforeEach
    void setUpTokenValidationMocks() {

        when(auth0Service.authenticate(eq(new AuthenticationRequestDto("valid@gmail.com", "valid"))))
                .thenReturn(new AuthenticationResponseDto(MOCKED_JWT.getTokenValue()));

        when(jwtDecoder.decode(eq(MOCKED_JWT.getTokenValue()))).thenReturn(MOCKED_JWT);
    }

    @Test
    public void getEngines_shouldReturnPaginatedEngines() {

        ParameterizedTypeReference<PagedModel<EntityModel<EngineResponseDto>>> responseType =
                new ParameterizedTypeReference<>() {};

        ResponseEntity<PagedModel<EntityModel<EngineResponseDto>>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PagedModel<EntityModel<EngineResponseDto>> pagedModel = response.getBody();

        assertThat(pagedModel).isNotNull();
        assertThat(pagedModel.getContent()).isNotNull().hasSize(1);

        EngineResponseDto firstEngine = pagedModel.getContent().iterator().next().getContent();

        assertThat(firstEngine).isNotNull();
        assertThat(firstEngine.getId()).isNotNull();
        assertThat(firstEngine.getName()).isNotNull();
        assertThat(firstEngine.getType()).isNotNull();
        assertThat(firstEngine.getCapacity()).isNotNull();

    }

    @Test
    public void createEngine_shouldReturnNoContent_whenEngineIsCreatedSuccessfully() {
        EngineCreateRequestDto engineCreateRequestDto = createEngineCreateRequestDto();

        HttpEntity<EngineCreateRequestDto> request = new HttpEntity<>(engineCreateRequestDto, AUTH_HEADER);


        ResponseEntity<EngineResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                EngineResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(engineCreateRequestDto).usingRecursiveComparison().isEqualTo(response.getBody());
    }

    @Test
    public void createEngine_shouldReturnBadRequest_whenValidationFails() {
        EngineCreateRequestDto invalidEngineCreateRequestDto = new EngineCreateRequestDto();


        HttpEntity<EngineCreateRequestDto> request = new HttpEntity<>(invalidEngineCreateRequestDto, AUTH_HEADER);

        ResponseEntity<EngineResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                EngineResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteEngine_shouldReturnNoContent_whenEngineIsDeletedSuccessfully() {
        Engine engine = createEngine();
        engine.setId(null);
        engineService.save(engine);

        HttpEntity<Void> request = new HttpEntity<>(AUTH_HEADER);

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                engine.getId()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        assertThatThrownBy(() -> engineService.findById(engine.getId()))
                .isInstanceOf(EngineNotFoundException.class);

    }

    @Test
    public void deleteEngine_shouldReturnNotFound_whenEngineDoesNotExist() {
        Long nonExistingEngineId = 999L;

        HttpEntity<EngineCreateRequestDto> request = new HttpEntity<>(AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                nonExistingEngineId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateEngine_shouldReturnNoContent_whenEngineIsUpdatedSuccessfully() {

        long engineId = 1L;

        EngineUpdateRequestDto engineUpdateRequestDto = createEngineUpdateRequestDto();
        engineUpdateRequestDto.setName("Updated Engine Name");

        HttpEntity<EngineUpdateRequestDto> request = new HttpEntity<>(engineUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<EngineResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                EngineResponseDto.class,
                engineId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Engine savedEngine = engineService.findById(engineId);
        assertThat(savedEngine).usingRecursiveComparison().isEqualTo(response.getBody());
    }


    @Test
    public void updateEngine_shouldReturnNotFound_whenEngineDoesNotExist() {
        Long nonExistingEngineId = 999L;
        EngineUpdateRequestDto engineUpdateRequestDto = createEngineUpdateRequestDto();
        engineUpdateRequestDto.setName("Engine Name");

        HttpEntity<EngineUpdateRequestDto> request = new HttpEntity<>(engineUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<EngineResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                EngineResponseDto.class,
                nonExistingEngineId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateEngine_shouldReturnBadRequest_whenValidationFails() {

        long engineId = 1L;

        EngineUpdateRequestDto invalidEngineUpdateRequest = new EngineUpdateRequestDto();

        HttpEntity<EngineUpdateRequestDto> request = new HttpEntity<>(invalidEngineUpdateRequest, AUTH_HEADER);


        ResponseEntity<EngineResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                EngineResponseDto.class,
                engineId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
