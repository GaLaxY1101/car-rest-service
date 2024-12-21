package com.foxminded.korniichyk.car_rest_service.service.impl;

import com.foxminded.korniichyk.car_rest_service.dto.car.CarCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.CarNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.car.CarCreateRequestMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.car.CarResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Car;
import com.foxminded.korniichyk.car_rest_service.model.Category;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import com.foxminded.korniichyk.car_rest_service.repository.CarRepository;
import com.foxminded.korniichyk.car_rest_service.repository.CategoryRepository;
import com.foxminded.korniichyk.car_rest_service.repository.EngineRepository;
import com.foxminded.korniichyk.car_rest_service.repository.ModelRepository;
import com.foxminded.korniichyk.car_rest_service.service.contract.CrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.foxminded.korniichyk.car_rest_service.specification.CarSpecification.hasCategory;
import static com.foxminded.korniichyk.car_rest_service.specification.CarSpecification.hasManufacturingYearInRange;
import static com.foxminded.korniichyk.car_rest_service.specification.CarSpecification.hasModel;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CarService implements CrudService<Car, Long> {

    private final CarRepository carRepository;
    private final CarResponseMapper carResponseMapper;
    private final ModelRepository modelRepository;
    private final EngineRepository engineRepository;
    private final CategoryRepository categoryRepository;
    private final CarCreateRequestMapper carCreateRequestMapper;

    @Transactional
    @Override
    public void save(Car entity) {
        carRepository.save(entity);
        log.info("{} saved", entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        if (!carRepository.existsById(id)) {
            log.error("Car with id: '{}' not found", id);
            throw new CarNotFoundException("No such car");
        }

        carRepository.deleteById(id);
    }

    @Override
    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Car with id: '{}' not found", id);
                    return new CarNotFoundException("No such car");
                });
    }


    @Override
    public Page<Car> findAll(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    @Transactional
    public CarResponseDto createCar(CarCreateRequestDto carCreateRequestDto) {

        Car car = carCreateRequestMapper.toEntity(carCreateRequestDto);

        Model model = modelRepository.getReferenceById(carCreateRequestDto.getModelId());
        car.setModel(model);

        Engine engine = engineRepository.getReferenceById(carCreateRequestDto.getEngineId());
        car.setEngine(engine);

        Category category = categoryRepository.getReferenceById(carCreateRequestDto.getCategoryId());
        car.setCategory(category);

        save(car);

        return carResponseMapper.toDto(car);
    }

    @Transactional
    public CarResponseDto update(Long id, CarUpdateRequestDto carUpdateRequestDto) {

        Car car = findById(id);
        car.setColor(carUpdateRequestDto.getColor());
        car.setDrive(carUpdateRequestDto.getDrive());
        car.setSerialNumber(carUpdateRequestDto.getSerialNumber());
        car.setManufacturingDate(Instant.parse(carUpdateRequestDto.getManufacturingDate()));

        long categoryIdBeforeUpdate = car.getCategory().getId();
        long categoryIdAfterUpdate = carUpdateRequestDto.getCategoryId();

        if (categoryIdBeforeUpdate != categoryIdAfterUpdate) {
            Category category = categoryRepository.getReferenceById(categoryIdAfterUpdate);
            car.setCategory(category);
        }

        long engineIdBeforeUpdate = car.getEngine().getId();
        long engineIdAfterUpdate = carUpdateRequestDto.getEngineId();

        if (engineIdBeforeUpdate != engineIdAfterUpdate) {
            Engine engine = engineRepository.getReferenceById(engineIdAfterUpdate);
            car.setEngine(engine);
        }

        long modelIdBeforeUpdate = car.getModel().getId();
        long modelIdAfterUpdate = carUpdateRequestDto.getModelId();

        if (modelIdBeforeUpdate != modelIdAfterUpdate) {
            Model model = modelRepository.getReferenceById(modelIdAfterUpdate);
            car.setModel(model);
        }

        return carResponseMapper.toDto(car);

    }

    public Page<CarResponseDto> findAll(Pageable pageable,
                                        String model,
                                        String yearOfManufacturingFrom,
                                        String yearOfManufacturingTill,
                                        String category) {

        Specification<Car> specification = hasModel(model)
                .and(hasCategory(category))
                .and(hasManufacturingYearInRange(yearOfManufacturingFrom, yearOfManufacturingTill));

        return carRepository.findAll(specification, pageable)
                .map(carResponseMapper::toDto);

    }
}
