package com.foxminded.korniichyk.car_rest_service.api;

import com.foxminded.korniichyk.car_rest_service.configuration.TestContainersConfig;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.auth.AuthenticationResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.CategoryNotFoundException;
import com.foxminded.korniichyk.car_rest_service.model.Category;
import com.foxminded.korniichyk.car_rest_service.security.Auth0Service;
import com.foxminded.korniichyk.car_rest_service.service.impl.CategoryService;
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
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createCategory;
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
public class CategoryControllerTest {



    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryService categoryService;


    @MockBean
    private Auth0Service auth0Service;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final String BASE_URL = "/api/v1/categories";

    @BeforeEach
    void setUpTokenValidationMocks() {

        when(auth0Service.authenticate(eq(new AuthenticationRequestDto("valid@gmail.com", "valid"))))
                .thenReturn(new AuthenticationResponseDto(MOCKED_JWT.getTokenValue()));

        when(jwtDecoder.decode(eq(MOCKED_JWT.getTokenValue()))).thenReturn(MOCKED_JWT);
    }


    @Test
    public void getCategories_shouldReturnPaginatedCategories() {

        ParameterizedTypeReference<PagedModel<EntityModel<CategoryResponseDto>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<PagedModel<EntityModel<CategoryResponseDto>>> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                responseType
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PagedModel<EntityModel<CategoryResponseDto>> pagedModel = response.getBody();

        assertThat(pagedModel).isNotNull();
        assertThat(pagedModel.getContent()).isNotNull().hasSize(1);

        CategoryResponseDto firstCategory = pagedModel.getContent().iterator().next().getContent();

        assertThat(firstCategory).isNotNull();
        assertThat(firstCategory.getId()).isNotNull();
        assertThat(firstCategory.getName()).isNotNull();
    }


    @Test
    public void createCategory_shouldReturnNoContent_whenCategoryIsCreatedSuccessfully() {
        CategoryCreateRequestDto categoryCreateRequestDto = new CategoryCreateRequestDto();
        categoryCreateRequestDto.setName("New Category");

        HttpEntity<CategoryCreateRequestDto> request = new HttpEntity<>(categoryCreateRequestDto, AUTH_HEADER);


        ResponseEntity<CategoryResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                CategoryResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Category savedCategory = categoryService.findById(response.getBody().getId());
        assertThat(savedCategory).usingRecursiveComparison().isEqualTo(response.getBody());
    }

    @Test
    public void createCategory_shouldReturnBadRequest_whenValidationFails() {
        CategoryCreateRequestDto invalidCategoryCreateRequestDto = new CategoryCreateRequestDto();

        HttpEntity<CategoryCreateRequestDto> request = new HttpEntity<>(invalidCategoryCreateRequestDto, AUTH_HEADER);

        ResponseEntity<CategoryResponseDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                request,
                CategoryResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteCategory_shouldReturnNoContent_whenCategoryDeletedSuccessfully() {
        Category category = createCategory();
        category.setId(null);
        categoryService.save(category);


        HttpEntity<CategoryCreateRequestDto> request = new HttpEntity<>(AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                category.getId()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThatThrownBy(() -> categoryService.findById(category.getId())).isInstanceOf(CategoryNotFoundException.class);

    }

    @Test
    public void deleteCategory_shouldReturnNotFound_whenCategoryDoesNotExist() {
        Long nonExistingCategoryId = 999L;

        HttpEntity<CategoryCreateRequestDto> request = new HttpEntity<>(AUTH_HEADER);


        ResponseEntity<Void> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.DELETE,
                request,
                Void.class,
                nonExistingCategoryId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateCategory_shouldReturnNoContent_whenCategoryUpdatedSuccessfully() {

        long categoryId = 1L;

        CategoryUpdateRequestDto updateRequestDto = new CategoryUpdateRequestDto();
        updateRequestDto.setName("Updated Category Name");

        HttpEntity<CategoryUpdateRequestDto> request = new HttpEntity<>(updateRequestDto, AUTH_HEADER);

        ResponseEntity<CategoryResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                CategoryResponseDto.class,
                categoryId
        );


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo(updateRequestDto.getName());
        assertThat(categoryService.findById(categoryId).getName()).isEqualTo(updateRequestDto.getName());

    }

    @Test
    public void updateCategory_shouldReturnNotFound_whenCategoryDoesNotExist() {
        Long nonExistingCategoryId = 999L;

        CategoryUpdateRequestDto updateRequestDto = new CategoryUpdateRequestDto();
        updateRequestDto.setName("Category Name");

        HttpEntity<CategoryUpdateRequestDto> request = new HttpEntity<>(updateRequestDto, AUTH_HEADER);


        ResponseEntity<CategoryResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                CategoryResponseDto.class,
                nonExistingCategoryId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    public void updateCategory_shouldReturnBadRequest_whenValidationFails() {
        long categoryId = 1L;

        CategoryUpdateRequestDto invalidCategoryUpdateRequestDto = new CategoryUpdateRequestDto();

        HttpEntity<CategoryUpdateRequestDto> request = new HttpEntity<>(invalidCategoryUpdateRequestDto, AUTH_HEADER);


        ResponseEntity<CategoryResponseDto> response = restTemplate.exchange(
                BASE_URL + "/{id}",
                HttpMethod.PUT,
                request,
                CategoryResponseDto.class,
                categoryId
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
