package com.foxminded.korniichyk.car_rest_service.api;

import com.foxminded.korniichyk.car_rest_service.configuration.TestContainersConfig;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import com.foxminded.korniichyk.car_rest_service.security.Auth0Service;
import com.foxminded.korniichyk.car_rest_service.service.impl.ModelService;
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
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createModelCreateRequestDto;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createModelUpdateRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(classes = TestContainersConfig.class)
@Sql(scripts = {"/db/scripts/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/db/scripts/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ModelControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ModelService modelService;

    @MockBean
    private Auth0Service auth0Service;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final String BASE_URL = "/api/v1/models";


    @BeforeEach
    void setUpTokenValidationMocks() {

        when(auth0Service.authenticate(eq(new AuthenticationRequestDto("valid@gmail.com", "valid"))))
                .thenReturn(new AuthenticationResponseDto(MOCKED_JWT.getTokenValue()));

        when(jwtDecoder.decode(eq(MOCKED_JWT.getTokenValue()))).thenReturn(MOCKED_JWT);
    }

    @Test
    public void getModels_shouldReturnPaginatedModels() {

        ParameterizedTypeReference<PagedModel<EntityModel<ModelResponseDto>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<PagedModel<EntityModel<ModelResponseDto>>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PagedModel<EntityModel<ModelResponseDto>> pagedModel = response.getBody();

        assertThat(pagedModel).isNotNull();
        assertThat(pagedModel.getContent()).isNotNull().hasSize(1);

        ModelResponseDto firstModel = pagedModel.getContent().iterator().next().getContent();

        assertThat(firstModel).isNotNull();
        assertThat(firstModel.getId()).isNotNull();
        assertThat(firstModel.getName()).isNotNull();
        assertThat(firstModel.getGeneration()).isNotNull();
        assertThat(firstModel.getStartManufacturing()).isNotNull();
        assertThat(firstModel.getEndManufacturing()).isNotNull();
        assertThat(firstModel.getBrandName()).isNotNull();


    }


    @Test
    public void createModel_shouldReturnNoContent_whenModelIsCreatedSuccessfully() {
        ModelCreateRequestDto modelCreateRequestDto = createModelCreateRequestDto();

        HttpEntity<ModelCreateRequestDto> request = new HttpEntity<>(modelCreateRequestDto, AUTH_HEADER);

        ResponseEntity<ModelResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                ModelResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ModelResponseDto modelResponseDto = response.getBody();
        Model savedModel = modelService.findById(modelResponseDto.getId());

        assertThat(savedModel)
                .usingRecursiveComparison()
                .ignoringFields("id", "brand","startManufacturing","endManufacturing")
                .isEqualTo(modelCreateRequestDto);

        assertThat(savedModel.getStartManufacturing()).isEqualTo(modelCreateRequestDto.getStartManufacturing());
        assertThat(savedModel.getEndManufacturing()).isEqualTo(modelCreateRequestDto.getEndManufacturing());
        assertThat(savedModel.getBrand().getId()).isEqualTo(modelCreateRequestDto.getBrandId());
    }

    @Test
    public void createModel_shouldReturnBadRequest_whenValidationFails() {
        ModelCreateRequestDto invalidModelCreateRequestDto = new ModelCreateRequestDto();

        HttpEntity<ModelCreateRequestDto> request = new HttpEntity<>(invalidModelCreateRequestDto, AUTH_HEADER);

        ResponseEntity<ModelResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                ModelResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteModel_shouldReturnNotFound_whenModelDoesNotExist() {
        Long nonExistingModelId = 999L;

        HttpEntity<Void> request = new HttpEntity<>(AUTH_HEADER);

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                nonExistingModelId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateModel_shouldReturnUpdatedEntity_whenModelUpdatedSuccessfully() {
        Long modelId = 1L;
        ModelUpdateRequestDto modelUpdateRequestDto = createModelUpdateRequestDto();

        HttpEntity<ModelUpdateRequestDto> request = new HttpEntity<>(modelUpdateRequestDto, AUTH_HEADER);

        ResponseEntity<ModelResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                ModelResponseDto.class,
                modelId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Model savedModel = modelService.findById(response.getBody().getId());

        assertThat(savedModel)
                .usingRecursiveComparison()
                .ignoringFields("id", "brand","startManufacturing","endManufacturing")
                .isEqualTo(modelUpdateRequestDto);

        assertThat(savedModel.getStartManufacturing()).isEqualTo(modelUpdateRequestDto.getStartManufacturing());
        assertThat(savedModel.getEndManufacturing()).isEqualTo(modelUpdateRequestDto.getEndManufacturing());
        assertThat(savedModel.getBrand().getId()).isEqualTo(modelUpdateRequestDto.getBrandId());
    }

    @Test
    public void updateModel_shouldReturnBadRequest_whenValidationFails() {
        Long modelId = 1L;

        ModelCreateRequestDto invalidModelUpdateRequestDto = new ModelCreateRequestDto();

        HttpEntity<ModelCreateRequestDto> request = new HttpEntity<>(invalidModelUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<ModelResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                ModelResponseDto.class,
                modelId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateModel_shouldReturnNotFound_whenModelDoesNotExist() {

        Long nonExistingModelId = 999L;
        ModelUpdateRequestDto modelUpdateRequestDto = createModelUpdateRequestDto();

        HttpEntity<ModelUpdateRequestDto> request = new HttpEntity<>(modelUpdateRequestDto, AUTH_HEADER);

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                Void.class,
                nonExistingModelId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
