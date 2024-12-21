package com.foxminded.korniichyk.car_rest_service.service;

import com.foxminded.korniichyk.car_rest_service.dto.model.ModelCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.ModelNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.model.ModelCreateRequestMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.model.ModelCreateRequestMapperImpl;
import com.foxminded.korniichyk.car_rest_service.mapper.model.ModelResponseMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.model.ModelResponseMapperImpl;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import com.foxminded.korniichyk.car_rest_service.repository.BrandRepository;
import com.foxminded.korniichyk.car_rest_service.repository.ModelRepository;
import com.foxminded.korniichyk.car_rest_service.service.impl.BrandService;
import com.foxminded.korniichyk.car_rest_service.service.impl.ModelService;
import com.foxminded.korniichyk.car_rest_service.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createBrand;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createModelCreateRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModelServiceTest {

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;

    @Spy
    private ModelResponseMapper modelResponseMapper = new ModelResponseMapperImpl();

    @Spy
    private ModelCreateRequestMapper modelCreateRequestMapper = new ModelCreateRequestMapperImpl();

    @InjectMocks
    private ModelService modelService;

    @Test
    public void delete_shouldThrowModelNotFoundException_whenModelDoesNotExist() {
        when(modelRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(ModelNotFoundException.class, () -> modelService.delete(1L));
        verify(modelRepository, never()).delete(any(Model.class));
    }

    @Test
    public void delete_shouldDeleteModel_whenExists() {
        Model model = TestUtil.createModel();
        when(modelRepository.existsById(anyLong())).thenReturn(true);
        modelService.delete(model.getId());

        verify(modelRepository).deleteById(model.getId());
    }

    @Test
    public void findById_shouldThrowModelNotFoundException_whenModelDoesNotExist() {
        when(modelRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ModelNotFoundException.class, () -> modelService.findById(1L));
    }

    @Test
    public void findById_shouldReturnModel_whenModelExists() {
        Model model = TestUtil.createModel();
        when(modelRepository.findById(anyLong())).thenReturn(Optional.of(model));

        Model foundModel = modelService.findById(model.getId());

        assertNotNull(foundModel);
        assertEquals(model.getId(), foundModel.getId());
        verify(modelRepository).findById(model.getId());
    }

    @Test
    public void createModel_shouldCreateModelWithCorrectDetails() {
        ModelCreateRequestDto requestDto = createModelCreateRequestDto();
        Brand brand = createBrand();

        when(brandService.findById(requestDto.getBrandId())).thenReturn(brand);

        when(modelRepository.save(any(Model.class))).thenAnswer(invocation -> {
            Model modelFromArgument = invocation.getArgument(0);
            modelFromArgument.setId(1L);
            return modelFromArgument;
        });

        ModelResponseDto createdModel = modelService.createModel(requestDto);

        assertNotNull(createdModel);
        assertEquals(requestDto.getName(), createdModel.getName());
        assertEquals(requestDto.getGeneration(), createdModel.getGeneration());
        assertEquals(brand.getName(), createdModel.getBrandName());
    }

    @Test
    public void update_shouldUpdateModelDetails_whenModelExists() {
        Model model = TestUtil.createModel();
        Long modelId = model.getId();
        ModelUpdateRequestDto updateRequestDto = TestUtil.createModelUpdateRequestDto();
        updateRequestDto.setName("New Model Name");
        updateRequestDto.setGeneration("New Generation");
        updateRequestDto.setBrandId(10L);

        when(modelRepository.findByIdWithLock(modelId)).thenReturn(Optional.of(model));
        when(brandRepository.getReferenceById(anyLong())).thenReturn(createBrand());

        ModelResponseDto modelResponseDto = modelService.update(modelId, updateRequestDto);

        assertEquals(modelResponseDto.getName(), updateRequestDto.getName());
        assertEquals(modelResponseDto.getGeneration(), updateRequestDto.getGeneration());
        verify(modelRepository).findByIdWithLock(modelId);
    }

}
