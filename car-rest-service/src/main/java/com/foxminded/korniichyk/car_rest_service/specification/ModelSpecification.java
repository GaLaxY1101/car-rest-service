package com.foxminded.korniichyk.car_rest_service.specification;

import com.foxminded.korniichyk.car_rest_service.model.Brand;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class ModelSpecification {

    public static Specification<Model> hasName(String name) {
        return (Root<Model> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String lowerCaseName = name.trim().toLowerCase();
            return criteriaBuilder.equal(
                    criteriaBuilder.function("LOWER", String.class, root.get("name")),
                    lowerCaseName
            );
        };
    }

    public static Specification<Model> hasGeneration(String generation) {
        return (Root<Model> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (generation == null || generation.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            String lowerCaseGeneration = generation.trim().toLowerCase();
            return criteriaBuilder
                    .equal(
                            criteriaBuilder.function("LOWER", String.class, root.get("generation")),
                            lowerCaseGeneration
                    );
        };
    }
    public static Specification<Model> hasBrand(String brandName) {
        return (Root<Model> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (brandName == null || brandName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Model, Brand> brandJoin = root.join("brand");

            String lowerCaseBrandName = brandName.trim().toLowerCase();


            return criteriaBuilder.equal(
                    criteriaBuilder.lower(brandJoin.get("name")),
                    lowerCaseBrandName
            );
        };
    }


}
