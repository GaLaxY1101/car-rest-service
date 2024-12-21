package com.foxminded.korniichyk.car_rest_service.service.impl;

import com.foxminded.korniichyk.car_rest_service.dto.model.ModelCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.ModelNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.model.ModelCreateRequestMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.model.ModelResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import com.foxminded.korniichyk.car_rest_service.repository.BrandRepository;
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

import static com.foxminded.korniichyk.car_rest_service.specification.ModelSpecification.hasBrand;
import static com.foxminded.korniichyk.car_rest_service.specification.ModelSpecification.hasGeneration;
import static com.foxminded.korniichyk.car_rest_service.specification.ModelSpecification.hasName;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelService implements CrudService<Model, Long> {

    private final BrandService brandService;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final ModelResponseMapper modelResponseMapper;
    private final ModelCreateRequestMapper modelCreateRequestMapper;

    @Transactional
    @Override
    public void save(Model entity) {
        modelRepository.save(entity);
        log.info("{} saved", entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        if (!modelRepository.existsById(id)) {
            log.error("Model with id: '{}' not found", id);
            throw new ModelNotFoundException("No such model");
        }

        modelRepository.deleteById(id);
    }

    @Override
    public Model findById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Engine with id: '{}' not found", id);
                    return new ModelNotFoundException("No such model");
                });
    }

    public Model findByIdWithLock(Long id) {
        return modelRepository.findByIdWithLock(id)
                .orElseThrow(() -> {
                    log.error("Engine with id: '{}' not found", id);
                    return new ModelNotFoundException("No such model");
                });
    }

    @Override
    public Page<Model> findAll(Pageable pageable) {
        return modelRepository.findAll(pageable);
    }

    @Transactional
    public ModelResponseDto createModel(ModelCreateRequestDto modelCreateRequestDto) {

        Model model = modelCreateRequestMapper.toEntity(modelCreateRequestDto);


        Brand brand = brandService.findById(modelCreateRequestDto.getBrandId());
        model.setBrand(brand);
        save(model);

        return modelResponseMapper.toDto(model);
    }

    @Transactional
    public ModelResponseDto update(Long id, ModelUpdateRequestDto modelUpdateRequestDto) {


        Model model = findByIdWithLock(id);

        model.setName(modelUpdateRequestDto.getName());
        model.setGeneration(modelUpdateRequestDto.getGeneration());

        Instant startManufacturing = Instant.parse(modelUpdateRequestDto.getStartManufacturing());
        Instant endManufacturing = Instant.parse(modelUpdateRequestDto.getEndManufacturing());

        model.setStartManufacturing(startManufacturing);
        model.setEndManufacturing(endManufacturing);

        Long brandIdBeforeUpdate = model.getBrand().getId();
        Long brandIdAfterUpdate = modelUpdateRequestDto.getBrandId();

        if (!brandIdBeforeUpdate.equals(brandIdAfterUpdate)) {
            Brand brand = brandRepository.getReferenceById(brandIdAfterUpdate);
            model.setBrand(brand);
        }

        return modelResponseMapper.toDto(model);

    }

    public Page<ModelResponseDto> findAll(Pageable pageable, String name, String generation, String brand) {
        Specification<Model> specification = hasName(name)
                .and(hasGeneration(generation))
                .and(hasBrand(brand));

        return modelRepository.findAll(specification, pageable)
                .map(modelResponseMapper::toDto);
    }
}
