package com.foxminded.korniichyk.car_rest_service.util;

import com.foxminded.korniichyk.car_rest_service.dto.brand.BrandResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.car.CarUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.category.CategoryResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineResponseDto;
import com.foxminded.korniichyk.car_rest_service.dto.engine.EngineUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelCreateRequestDto;
import com.foxminded.korniichyk.car_rest_service.dto.model.ModelUpdateRequestDto;
import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.model.Car;
import com.foxminded.korniichyk.car_rest_service.model.Category;
import com.foxminded.korniichyk.car_rest_service.model.Engine;
import com.foxminded.korniichyk.car_rest_service.model.Model;

import java.time.Instant;

public class TestUtil {

    public static Brand createBrand() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand1111");
        return brand;
    }

    public static Car createCar() {
        Car car = new Car();
        car.setId(1L);
        car.setColor("Yellow");
        car.setDrive(Car.Drive.ALL);
        car.setSerialNumber("ABC123456789");
        car.setManufacturingDate(Instant.parse("2024-01-01T00:00:00Z"));
        car.setModel(createModel());
        car.setEngine(createEngine());
        car.setCategory(createCategory());
        return car;
    }

    public static Category createCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category");
        return category;
    }

    public static Engine createEngine() {
        Engine engine = new Engine();
        engine.setId(1L);
        engine.setName("Engine1");
        engine.setCapacity(3.0);
        engine.setType(Engine.Type.DIESEL);
        return engine;
    }

    public static Model createModel() {
        Model model = new Model();
        model.setId(1L);
        model.setName("Model1");
        model.setBrand(createBrand());
        model.setGeneration("Gen 1");
        model.setStartManufacturing(Instant.parse("2010-01-01T00:00:00Z"));
        model.setEndManufacturing(Instant.parse("2020-01-01T00:00:00Z"));

        return model;
    }

    public static CarCreateRequestDto createCarRequestDto() {
        CarCreateRequestDto carCreateRequestDto = new CarCreateRequestDto();
        carCreateRequestDto.setColor("Red");
        carCreateRequestDto.setDrive(Car.Drive.ALL);
        carCreateRequestDto.setSerialNumber("asdsad21");
        carCreateRequestDto.setManufacturingDate("2010-01-01T00:00:00Z");
        carCreateRequestDto.setEngineId(1L);
        carCreateRequestDto.setModelId(1L);
        carCreateRequestDto.setCategoryId(1L);
        return carCreateRequestDto;
    }

    public static EngineCreateRequestDto createEngineRequestDto() {
        EngineCreateRequestDto engineCreateRequestDto = new EngineCreateRequestDto();
        engineCreateRequestDto.setName("Engine1");
        engineCreateRequestDto.setCapacity(3.0);
        engineCreateRequestDto.setType(Engine.Type.DIESEL);
        return engineCreateRequestDto;
    }

    public static ModelCreateRequestDto createModelCreateRequestDto() {

        ModelCreateRequestDto modelCreateRequestDto = new ModelCreateRequestDto();
        modelCreateRequestDto.setName("Model1");
        modelCreateRequestDto.setGeneration("Gen1");
        modelCreateRequestDto.setStartManufacturing("2010-01-01T00:00:00Z");
        modelCreateRequestDto.setEndManufacturing("2015-01-01T00:00:00Z");
        modelCreateRequestDto.setBrandId(1L);
        return  modelCreateRequestDto;
    }

    public static ModelUpdateRequestDto createModelUpdateRequestDto() {
        ModelUpdateRequestDto modelUpdateRequestDto = new ModelUpdateRequestDto();
        modelUpdateRequestDto.setBrandId(1L);
        modelUpdateRequestDto.setGeneration("Gen1");
        modelUpdateRequestDto.setStartManufacturing("2010-01-01T00:00:00Z");
        modelUpdateRequestDto.setEndManufacturing("2020-01-01T00:00:00Z");
        modelUpdateRequestDto.setName("Model 3");

        return modelUpdateRequestDto;
    }

    public static BrandResponseDto createBrandResponseDto() {
        BrandResponseDto brandResponseDto = new BrandResponseDto();
        brandResponseDto.setId(1L);
        brandResponseDto.setName("Brand1");
        return brandResponseDto;
    }

    public static CategoryResponseDto createCategoryResponseDto() {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(1L);
        categoryResponseDto.setName("Category1");
        return categoryResponseDto;
    }

    public static EngineResponseDto createEngineResponseDto() {
        EngineResponseDto engineResponseDto = new EngineResponseDto();
        engineResponseDto.setId(1L);
        engineResponseDto.setName("Engine1");
        engineResponseDto.setCapacity(3.0);
        return engineResponseDto;
    }

    public static EngineCreateRequestDto createEngineCreateRequestDto() {
        EngineCreateRequestDto engineCreateRequestDto = new EngineCreateRequestDto();
        engineCreateRequestDto.setName("New Engine");
        engineCreateRequestDto.setCapacity(3.0);
        engineCreateRequestDto.setType(Engine.Type.DIESEL);
        return engineCreateRequestDto;
    }

    public static CarCreateRequestDto createCarCreateRequestDto() {
        CarCreateRequestDto carCreateRequestDto = new CarCreateRequestDto();
        carCreateRequestDto.setModelId(1L);
        carCreateRequestDto.setCategoryId(1L);
        carCreateRequestDto.setEngineId(1L);
        carCreateRequestDto.setManufacturingDate("2020-01-01T00:00:00Z");
        carCreateRequestDto.setDrive(Car.Drive.ALL);
        carCreateRequestDto.setColor("Black");
        carCreateRequestDto.setSerialNumber("serialNumber");
        return carCreateRequestDto;
    }

    public static CarUpdateRequestDto createCarUpdateRequestDto() {
        CarUpdateRequestDto carUpdateRequestDto = new CarUpdateRequestDto();
        carUpdateRequestDto.setModelId(1L);
        carUpdateRequestDto.setCategoryId(1L);
        carUpdateRequestDto.setEngineId(1L);
        carUpdateRequestDto.setManufacturingDate("2020-01-01T00:00:00Z");
        carUpdateRequestDto.setDrive(Car.Drive.ALL);
        carUpdateRequestDto.setColor("Black");
        carUpdateRequestDto.setSerialNumber("serialNumber");
        return carUpdateRequestDto;
    }

    public static EngineUpdateRequestDto createEngineUpdateRequestDto() {
        EngineUpdateRequestDto engineUpdateRequestDto = new EngineUpdateRequestDto();
        engineUpdateRequestDto.setName("New Engine");
        engineUpdateRequestDto.setCapacity(3.0);
        engineUpdateRequestDto.setType(Engine.Type.DIESEL);
        return engineUpdateRequestDto;
    }
}
