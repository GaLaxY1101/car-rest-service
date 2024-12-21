package com.foxminded.korniichyk.car_rest_service.api;

import com.foxminded.korniichyk.car_rest_service.configuration.TestContainersConfig;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.CarNotFoundException;
import com.foxminded.korniichyk.car_rest_service.model.Car;
import com.foxminded.korniichyk.car_rest_service.security.Auth0Service;
import com.foxminded.korniichyk.car_rest_service.service.impl.CarService;
import com.foxminded.korniichyk.car_rest_service.util.TestUtil;
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
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createCarUpdateRequestDto;
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
public class CarControllerTest {

    @Autowired
    private CarService carService;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private Auth0Service auth0Service;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final String BASE_URL = "/api/v1/cars";


    @BeforeEach
    void setUpTokenValidationMocks() {

        when(auth0Service.authenticate(eq(new AuthenticationRequestDto("valid@gmail.com", "valid"))))
                .thenReturn(new AuthenticationResponseDto(MOCKED_JWT.getTokenValue()));

        when(jwtDecoder.decode(eq(MOCKED_JWT.getTokenValue()))).thenReturn(MOCKED_JWT);
    }

    @Test
    public void getCars_shouldReturnPaginatedCars() {

        ParameterizedTypeReference<PagedModel<EntityModel<CarResponseDto>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<PagedModel<EntityModel<CarResponseDto>>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PagedModel<EntityModel<CarResponseDto>> pagedModel = response.getBody();

        assertThat(pagedModel).isNotNull();
        assertThat(pagedModel.getContent()).isNotNull().hasSize(1);

        CarResponseDto firstCar = pagedModel.getContent().iterator().next().getContent();

        assertThat(firstCar).isNotNull();
        assertThat(firstCar.getId()).isNotNull();
        assertThat(firstCar.getColor()).isNotNull();
        assertThat(firstCar.getDrive()).isNotNull();
        assertThat(firstCar.getSerialNumber()).isNotNull();
        assertThat(firstCar.getEngineCapacity()).isNotNull();
        assertThat(firstCar.getEngineName()).isNotNull();
        assertThat(firstCar.getCategoryName()).isNotNull();
    }


    @Test
    public void createCar_shouldReturnOk_whenCarIsCreatedSuccessfully() {

        CarCreateRequestDto carCreateRequestDto = TestUtil.createCarRequestDto();

        HttpEntity<CarCreateRequestDto> request = new HttpEntity<>(carCreateRequestDto, AUTH_HEADER);

        ResponseEntity<CarResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                CarResponseDto.class
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Car savedCar = carService.findById(response.getBody().getId());
        assertThat(savedCar)
                .usingRecursiveComparison()
                .comparingOnlyFields(
                        "color",
                        "serialNumber",
                        "drive"
                )
                .isEqualTo(carCreateRequestDto);

        assertThat(savedCar.getManufacturingDate()).isEqualTo(carCreateRequestDto.getManufacturingDate());
        assertThat(savedCar.getModel().getId()).isEqualTo(carCreateRequestDto.getModelId());
        assertThat(savedCar.getEngine().getId()).isEqualTo(carCreateRequestDto.getEngineId());
        assertThat(savedCar.getCategory().getId()).isEqualTo(carCreateRequestDto.getCategoryId());

    }

    @Test
    public void createCar_shouldReturnBadRequest_whenValidationFails() {
        CarCreateRequestDto invalidCarCreateRequestDto = new CarCreateRequestDto();

        HttpEntity<CarCreateRequestDto> request = new HttpEntity<>(invalidCarCreateRequestDto, AUTH_HEADER);

        ResponseEntity<CarResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                CarResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteCars_shouldReturnNoContent_whenCarIsDeletedSuccessfully() {
        Long carId = 1L;

        HttpEntity<CarCreateRequestDto> request = new HttpEntity<>(AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                carId
        );

        assertThatThrownBy(() -> carService.findById(1L)).isInstanceOf(CarNotFoundException.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void deleteCars_shouldReturnNotFound_whenCarDoesNotExist() {
        Long nonExistingCarId = 999L;

        HttpEntity<CarCreateRequestDto> request = new HttpEntity<>(AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                nonExistingCarId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateCar_shouldReturnOk_whenCarUpdatedSuccessfully() {
        Long carId = 1L;
        CarUpdateRequestDto carUpdateRequestDto = createCarUpdateRequestDto();

        HttpEntity<CarUpdateRequestDto> request = new HttpEntity<>(carUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<CarResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                CarResponseDto.class,
                carId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Car savedCar = carService.findById(response.getBody().getId());
        assertThat(savedCar)
                .usingRecursiveComparison()
                .comparingOnlyFields(
                        "color",
                        "serialNumber",
                        "drive"
                )
                .isEqualTo(carUpdateRequestDto);

        assertThat(savedCar.getManufacturingDate()).isEqualTo(carUpdateRequestDto.getManufacturingDate());
        assertThat(savedCar.getModel().getId()).isEqualTo(carUpdateRequestDto.getModelId());
        assertThat(savedCar.getEngine().getId()).isEqualTo(carUpdateRequestDto.getEngineId());
        assertThat(savedCar.getCategory().getId()).isEqualTo(carUpdateRequestDto.getCategoryId());
    }

    @Test
    public void updateCar_shouldReturnBadRequest_whenValidationFails() {
        Long carId = 1L;
        CarCreateRequestDto invalidCarUpdateRequestDto = new CarCreateRequestDto();

        HttpEntity<CarCreateRequestDto> request = new HttpEntity<>(invalidCarUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                Void.class,
                carId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void updateCar_shouldReturnNotFound_whenCarDoesNotExist() {
        Long nonExistingCarId = 999L;
        CarUpdateRequestDto carUpdateRequestDto = createCarUpdateRequestDto();

        HttpEntity<CarUpdateRequestDto> request = new HttpEntity<>(carUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                Void.class,
                nonExistingCarId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
