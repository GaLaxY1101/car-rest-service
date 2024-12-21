package com.foxminded.korniichyk.car_rest_service.service;

import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.BrandNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.brand.BrandResponseMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.brand.BrandResponseMapperImpl;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.repository.BrandRepository;
import com.foxminded.korniichyk.car_rest_service.service.impl.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createBrand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Spy
    private BrandResponseMapper brandResponseDto = new BrandResponseMapperImpl();

    @InjectMocks
    private BrandService brandService;

    @Test
    public void delete_shouldThrowBrandNotFoundException_whenBrandDoesntExist() {

        when(brandRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(BrandNotFoundException.class, () -> brandService.delete(1L));
        verify(brandRepository, never()).delete(any());
    }

    @Test
    public void delete_shouldDeleteBrand_whenExists() {

        Brand brand = createBrand();
        when(brandRepository.existsById(anyLong())).thenReturn(true);

        brandService.delete(brand.getId());
        verify(brandRepository).deleteById(brand.getId());
    }

    @Test
    public void findById_shouldThrowBrandNotFoundException_whenBrandDoesntExist() {

        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BrandNotFoundException.class, () -> brandService.findById(1L));
    }


    @Test
    public void update_shouldUpdateBrandName_whenBrandExists() {

        Brand brand = createBrand();
        Long brandId = brand.getId();

        BrandUpdateRequestDto brandUpdateRequestDto = new BrandUpdateRequestDto();
        brandUpdateRequestDto.setName("Updated Brand");


        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        brandService.update(brandId, brandUpdateRequestDto);

        assertEquals("Updated Brand", brand.getName());
    }

    @Test
    public void createBrand_shouldReturnBrandWithCorrectName() {
        BrandCreateRequestDto createBrandRequestDto = new BrandCreateRequestDto();
        createBrandRequestDto.setName("New Brand");

        BrandResponseDto createdBrand = brandService.createBrand(createBrandRequestDto);

        assertNotNull(createdBrand);
        assertEquals("New Brand", createdBrand.getName());
    }

}
