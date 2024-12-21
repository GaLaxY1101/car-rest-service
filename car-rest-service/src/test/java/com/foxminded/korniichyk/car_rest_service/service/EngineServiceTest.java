package com.foxminded.korniichyk.car_rest_service.service;

import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.exception.EngineNotFoundException;
import com.foxminded.korniichyk.car_rest_service.mapper.engine.EngineCreateRequestMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.engine.EngineCreateRequestMapperImpl;
import com.foxminded.korniichyk.car_rest_service.mapper.engine.EngineResponseMapper;
import com.foxminded.korniichyk.car_rest_service.mapper.engine.EngineResponseMapperImpl;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import com.foxminded.korniichyk.car_rest_service.repository.EngineRepository;
import com.foxminded.korniichyk.car_rest_service.service.impl.EngineService;
import com.foxminded.korniichyk.car_rest_service.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.foxminded.korniichyk.car_rest_service.util.TestUtil.createEngineRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EngineServiceTest {

    @Mock
    private EngineRepository engineRepository;

    @Spy
    private EngineResponseMapper engineResponseMapper = new EngineResponseMapperImpl();

    @Spy
    private EngineCreateRequestMapper  engineCreateRequestMapper = new EngineCreateRequestMapperImpl();

    @InjectMocks
    private EngineService engineService;

    @Test
    public void delete_shouldThrowEngineNotFoundException_whenEngineDoesNotExist() {
        when(engineRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EngineNotFoundException.class, () -> engineService.delete(1L));
        verify(engineRepository, never()).delete(any(Engine.class));
    }

    @Test
    public void delete_shouldDeleteEngine_whenExists() {
        Engine engine = TestUtil.createEngine();

        when(engineRepository.existsById(anyLong())).thenReturn(true);

        engineService.delete(engine.getId());

        verify(engineRepository).deleteById(engine.getId());
    }

    @Test
    public void findById_shouldThrowEngineNotFoundException_whenEngineDoesNotExist() {
        when(engineRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EngineNotFoundException.class, () -> engineService.findById(1L));
    }

    @Test
    public void findById_shouldReturnEngine_whenEngineExists() {
        Engine engine = TestUtil.createEngine();
        when(engineRepository.findById(anyLong())).thenReturn(Optional.of(engine));

        Engine foundEngine = engineService.findById(engine.getId());

        assertNotNull(foundEngine);
        assertEquals(engine.getId(), foundEngine.getId());
        verify(engineRepository).findById(engine.getId());
    }

    @Test
    public void createEngine_shouldCreateEngineWithCorrectDetails() {
        EngineCreateRequestDto createEngineRequestDto = createEngineRequestDto();

        when(engineRepository.save(any(Engine.class))).thenAnswer(invocation -> {
            Engine engine = invocation.getArgument(0);
            engine.setId(1L);
            return engine;
        });
        EngineResponseDto createdEngine = engineService.createEngine(createEngineRequestDto);

        assertEquals(createEngineRequestDto.getName(), createdEngine.getName());
        assertEquals(createEngineRequestDto.getCapacity(), createdEngine.getCapacity());
        verify(engineRepository).save(any(Engine.class));
    }

    @Test
    public void update_shouldUpdateEngineDetails_whenEngineExists() {
        Engine engine = TestUtil.createEngine();
        Long engineId = engine.getId();
        EngineUpdateRequestDto engineUpdateRequestDto = TestUtil.createEngineUpdateRequestDto();
        engineUpdateRequestDto.setName("New Engine Name");
        engineUpdateRequestDto.setCapacity(2.5);

        when(engineRepository.findById(engineId)).thenReturn(Optional.of(engine));

        engineService.update(engineId, engineUpdateRequestDto);

        assertEquals(engineUpdateRequestDto.getName(), engine.getName());
        assertEquals(engineUpdateRequestDto.getCapacity(), engine.getCapacity());
    }

}
