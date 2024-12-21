package com.foxminded.korniichyk.car_rest_service.service.impl;

import com.foxminded.korniichyk.car_rest_service.aspect.TimeMetric;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.BrandNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.brand.BrandResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.repository.BrandRepository;
import com.foxminded.korniichyk.car_rest_service.service.contract.CrudService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
@Data
public class BrandService implements CrudService<Brand, Long> {

    private final BrandRepository brandRepository;

    private final BrandResponseMapper brandResponseMapper;

    @Transactional
    @Override
    public void save(Brand entity) {
        brandRepository.save(entity);
        log.info("{} saved", entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            log.error("Brand with id: '{}' not found", id);
            throw new BrandNotFoundException("No such brand");
        }

        brandRepository.deleteById(id);
    }

    @Override
    public Brand findById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Brand with id: '{}' not found", id);
                    return new BrandNotFoundException("No such brand");
                });
    }


    @TimeMetric
    @Override
    public Page<Brand> findAll(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }


    @Transactional
    public BrandResponseDto update(Long id, BrandUpdateRequestDto brandUpdateRequestDto) {
        Brand brand = findById(id);
        brand.setName(brandUpdateRequestDto.getName());
        return brandResponseMapper.toDto(brand);

    }

    @Transactional
    public BrandResponseDto createBrand(BrandCreateRequestDto createBrandRequestDto) {
        Brand brand = new Brand();
        brand.setName(createBrandRequestDto.getName());

        save(brand);

        return brandResponseMapper.toDto(brand);
    }

}
