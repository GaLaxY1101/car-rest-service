package com.foxminded.korniichyk.car_rest_service.service.impl;

import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.EngineNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.engine.EngineCreateRequestMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.engine.EngineResponseMapper;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import com.foxminded.korniichyk.car_rest_service.repository.EngineRepository;
import com.foxminded.korniichyk.car_rest_service.service.contract.CrudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EngineService implements CrudService<Engine, Long> {

    private final EngineRepository engineRepository;
    private final EngineResponseMapper engineResponseMapper;
    private final EngineCreateRequestMapper engineCreateRequestMapper;
    @Transactional
    @Override
    public void save(Engine entity) {
        engineRepository.save(entity);
        log.info("{} saved", entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {

        if (!engineRepository.existsById(id)) {
            log.error("Engine with id: '{}' not found", id);
            throw new EngineNotFoundException("No such engine");
        }

        engineRepository.deleteById(id);
    }

    @Override
    public Engine findById(Long id) {
        return engineRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Engine with id: '{}' not found", id);
                    return new EngineNotFoundException("No such engine");
                });
    }

    @Override
    public Page<Engine> findAll(Pageable pageable) {
        return engineRepository.findAll(pageable);
    }

    @Transactional
    public EngineResponseDto createEngine(@Valid EngineCreateRequestDto engineCreateRequestDto) {

        Engine engine = engineCreateRequestMapper.toEntity(engineCreateRequestDto);

        save(engine);
        return engineResponseMapper.toDto(engine);
    }

    @Transactional
    public EngineResponseDto update(Long id, EngineUpdateRequestDto engineUpdateRequestDto) {

        Engine engine = findById(id);

        engine.setName(engineUpdateRequestDto.getName());
        engine.setCapacity(engineUpdateRequestDto.getCapacity());
        engine.setType(engineUpdateRequestDto.getType());

        return engineResponseMapper.toDto(engine);
    }
}
