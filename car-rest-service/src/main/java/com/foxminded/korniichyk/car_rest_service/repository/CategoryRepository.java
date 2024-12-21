package com.foxminded.korniichyk.car_rest_service.repository;

import com.foxminded.korniichyk.car_rest_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
