package com.foxminded.korniichyk.car_rest_service.repository;

import com.foxminded.korniichyk.car_rest_service.model.Model;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;
@RepositoryRestResource(exported = false)
public interface ModelRepository extends JpaRepository<Model, Long>, JpaSpecificationExecutor<Model> {


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM Model m WHERE m.id = :id")
    Optional<Model> findByIdWithLock(@Param("id") Long id);

}
