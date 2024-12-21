package com.foxminded.korniichyk.car_rest_service.repository;

import com.foxminded.korniichyk.car_rest_service.model.Engine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface EngineRepository extends JpaRepository<Engine, Long> {
}
