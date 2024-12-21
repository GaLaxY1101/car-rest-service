package com.foxminded.korniichyk.car_rest_service.service;

import com.foxminded.korniichyk.car_rest_service.dto.car.CarCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.CarNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.car.CarCreateRequestMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.car.CarCreateRequestMapperImpl;
import com.foxminded.korniichyk.car_rest_service.mapper.car.CarResponseMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.car.CarResponseMapperImpl;
import com.foxminded.korniichyk.car_rest_service.model.Car;
import com.foxminded.korniichyk.car_rest_service.repository.CarRepository;
import com.foxminded.korniichyk.car_rest_service.repository.CategoryRepository;
import com.foxminded.korniichyk.car_rest_service.repository.EngineRepository;
import com.foxminded.korniichyk.car_rest_service.repository.ModelRepository;
import com.foxminded.korniichyk.car_rest_service.service.impl.CarService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createCar;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createCarRequestDto;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createCarUpdateRequestDto;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createCategory;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createEngine;
import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createModel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private EngineRepository engineRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Spy
    private CarResponseMapper carResponseMapper = new CarResponseMapperImpl();

    @Spy
    private CarCreateRequestMapper carCreateRequestMapper = new CarCreateRequestMapperImpl();

    @InjectMocks
    private CarService carService;

    @Test
    public void delete_shouldThrowCarNotFoundException_whenCarDoesntExist() {

        when(carRepository.existsById(anyLong())).thenReturn(false);

        assertThatThrownBy(() -> carService.delete(1L))
                .isInstanceOf(CarNotFoundException.class);

        verify(carRepository, never()).delete(any(Car.class));
    }

    @Test
    public void delete_shouldDeleteCar_whenExists() {
        Car car = createCar();
        when(carRepository.existsById(anyLong())).thenReturn(true);

        carService.delete(car.getId());

        verify(carRepository).deleteById(car.getId());
    }

    @Test
    public void findById_shouldThrowCarNotFoundException_whenCarDoesntExist() {
        long notExistingId = 999L;
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.findById(notExistingId))
                .isInstanceOf(CarNotFoundException.class);
    }

    @Test
    public void findById_shouldReturnCarResponseDto_whenCarExists() {
        Car car = createCar();
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        carService.findById(car.getId());

        assertThat(car).isNotNull();
        verify(carRepository).findById(1L);
    }

    @Test
    public void update_shouldUpdateCarDetails_whenCarExists() {
        Car car = createCar();
        Long carId = car.getId();

        CarUpdateRequestDto carUpdateRequestDto = createCarUpdateRequestDto();
        carUpdateRequestDto.setCategoryId(10L);
        carUpdateRequestDto.setEngineId(10L);
        carUpdateRequestDto.setModelId(10L);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(modelRepository.getReferenceById(anyLong())).thenReturn(createModel());
        when(engineRepository.getReferenceById(anyLong())).thenReturn(createEngine());
        when(categoryRepository.getReferenceById(anyLong())).thenReturn(createCategory());

        CarResponseDto carResponseDto = carService.update(carId, carUpdateRequestDto);

        assertThat(carResponseDto)
                .usingRecursiveComparison()
                .comparingOnlyFields("serialNumber","drive","color");
    }

    @Test
    public void createCar_shouldReturnCarWithCorrectDetails() {
        CarCreateRequestDto createCarRequestDto = createCarRequestDto();

        when(modelRepository.getReferenceById(anyLong())).thenReturn(createModel());
        when(engineRepository.getReferenceById(anyLong())).thenReturn(createEngine());
        when(categoryRepository.getReferenceById(anyLong())).thenReturn(createCategory());

        CarResponseDto createdCar = carService.createCar(createCarRequestDto);

        assertThat(createdCar).isNotNull();
        assertThat(createdCar.getColor()).isEqualTo(createCarRequestDto.getColor());
        assertThat(createdCar.getSerialNumber()).isEqualTo(createCarRequestDto.getSerialNumber());
        assertThat(createdCar.getDrive()).isEqualTo(createCarRequestDto.getDrive());
    }
}
