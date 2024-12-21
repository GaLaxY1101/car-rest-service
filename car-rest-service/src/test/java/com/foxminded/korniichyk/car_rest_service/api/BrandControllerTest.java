package com.foxminded.korniichyk.car_rest_service.api;

import com.foxminded.korniichyk.car_rest_service.configuration.TestContainersConfig;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.BrandNotFoundException;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.security.Auth0Service;
import com.foxminded.korniichyk.car_rest_service.service.impl.BrandService;
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
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;

import java.net.URI;

import static com.foxminded.korniichyk.car_rest_service.api.SecurityConstants.MOCKED_JWT;
import static com.foxminded.korniichyk.car_rest_service.api.SecurityConstants.AUTH_HEADER;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createBrand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(classes = TestContainersConfig.class)
@Sql(scripts = {"/db/scripts/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Sql(scripts = {"/db/scripts/initData.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BrandControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BrandService brandService;

    @MockBean
    private Auth0Service auth0Service;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final String BASE_URL = "/api/v1/brands";


    @BeforeEach
    void setUpTokenValidationMocks() {

        when(auth0Service.authenticate(eq(new AuthenticationRequestDto("valid@gmail.com", "valid"))))
                .thenReturn(new AuthenticationResponseDto(MOCKED_JWT.getTokenValue()));

        when(jwtDecoder.decode(eq(MOCKED_JWT.getTokenValue()))).thenReturn(MOCKED_JWT);
    }

    @Test
    public void getBrands_shouldReturnPaginatedBrands() {

        ParameterizedTypeReference<PagedModel<EntityModel<BrandResponseDto>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<PagedModel<EntityModel<BrandResponseDto>>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PagedModel<EntityModel<BrandResponseDto>> pagedModel = response.getBody();

        assertThat(pagedModel).isNotNull();
        assertThat(pagedModel.getContent()).isNotNull();
        BrandResponseDto firstBrand = pagedModel.getContent().iterator().next().getContent();

        assertThat(firstBrand).isNotNull();
        assertThat(firstBrand.getId()).isNotNull();
        assertThat(firstBrand.getName()).isNotNull();

    }


    @Test
    public void getBrand() {

        long brandId = 1L;

        ResponseEntity<BrandResponseDto> response = restTemplate.
                getForEntity(
                        BASE_URL + "/{id}",
                        BrandResponseDto.class,
                        brandId
                );


        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void createBrand_shouldReturnCreatedEntity_whenValid() {

        BrandCreateRequestDto validBrandCreateRequestDto = new BrandCreateRequestDto();
        String brandName = "Test Brand";
        validBrandCreateRequestDto.setName(brandName);

        RequestEntity<BrandCreateRequestDto> httpEntity = new RequestEntity<>(validBrandCreateRequestDto, AUTH_HEADER, HttpMethod.POST, URI.create(BASE_URL));

        ResponseEntity<BrandResponseDto> response = restTemplate.exchange(
                httpEntity,
                BrandResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Brand savedBrand = brandService.findById(response.getBody().getId());
        assertThat(validBrandCreateRequestDto).usingRecursiveComparison().isEqualTo(savedBrand);

    }


    @Test
    public void createBrand_shouldReturnBadRequest_whenInvalid() {

        BrandCreateRequestDto validBrandCreateRequestDto = new BrandCreateRequestDto();


        RequestEntity<BrandCreateRequestDto> httpEntity = new RequestEntity<>(validBrandCreateRequestDto, AUTH_HEADER, HttpMethod.POST, URI.create(BASE_URL));

        ResponseEntity<BrandResponseDto> response = restTemplate.exchange(
                httpEntity,
                BrandResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteBrand_shouldReturnNoContent_whenBrandDeleted() {

        Brand brand = createBrand();
        brand.setId(null);
        brandService.save(brand);


        HttpEntity<Void> httpEntity = new HttpEntity<>(AUTH_HEADER);

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                httpEntity,
                Void.class,
                brand.getId()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThatThrownBy(() -> brandService.findById(brand.getId()))
                .isInstanceOf(BrandNotFoundException.class);
    }


    @Test
    public void deleteBrand_shouldReturnNotFound_whenBrandDoesNotExist() {
        Long nonExistingBrandId = 999L;

        HttpEntity<Void> request = new HttpEntity<>(AUTH_HEADER);

        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                nonExistingBrandId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    public void updateBrand_shouldReturnNoContent_whenBrandUpdated() {

        long brandId = 1L;
        BrandUpdateRequestDto updateRequestDto = new BrandUpdateRequestDto();
        updateRequestDto.setName("Updated Brand Name");

        HttpEntity<BrandUpdateRequestDto> request = new HttpEntity<>(updateRequestDto, AUTH_HEADER);

        ResponseEntity<BrandResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                BrandResponseDto.class,
                brandId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Brand savedBrand = brandService.findById(brandId);
        assertThat(savedBrand).usingRecursiveComparison().isEqualTo(response.getBody());

    }

    @Test
    public void updateBrand_shouldReturnNotFound_whenBrandDoesNotExist() {
        Long nonExistingBrandId = 999L;
        BrandUpdateRequestDto updateRequestDto = new BrandUpdateRequestDto();
        updateRequestDto.setName("Brand Name");

        HttpEntity<BrandUpdateRequestDto> request = new HttpEntity<>(updateRequestDto, AUTH_HEADER);

        ResponseEntity<BrandResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                BrandResponseDto.class,
                nonExistingBrandId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateBrand_shouldReturnBadRequest_whenValidationFails() {

        long brandId = 1L;

        BrandUpdateRequestDto invalidBrandUpdateRequestDto = new BrandUpdateRequestDto();

        HttpEntity<BrandUpdateRequestDto> request = new HttpEntity<>(invalidBrandUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                Void.class,
                brandId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
