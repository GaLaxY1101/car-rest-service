package com.foxminded.korniichyk.car_rest_service.specification;

import com.foxminded.korniichyk.car_rest_service.model.Car;
import com.foxminded.korniichyk.car_rest_service.model.Model;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneId;

public class CarSpecification {

    public static Specification<Car> hasModel(String modelName) {
        return (Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (modelName == null || modelName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Car, Model> brandJoin = root.join("model");

            String lowerCaseBrandName = modelName.trim().toLowerCase();

            return criteriaBuilder.equal(
                    criteriaBuilder.function("LOWER", String.class, brandJoin.get("name")),
                    lowerCaseBrandName
            );
        };
    }

    public static Specification<Car> hasCategory(String categoryName) {
        return (Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (categoryName == null || categoryName.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            Join<Car, Model> brandJoin = root.join("category");

            String lowerCaseBrandName = categoryName.trim().toLowerCase();

            return criteriaBuilder.equal(
                    criteriaBuilder.function("LOWER", String.class, brandJoin.get("name")),
                    lowerCaseBrandName
            );
        };
    }


    public static Specification<Car> hasManufacturingYearInRange(String yearOfManufacturingFrom, String yearOfManufacturingTill) {
        return (Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {

            Integer fromYear = (yearOfManufacturingFrom != null && !yearOfManufacturingFrom.isEmpty()) ?
                    Integer.parseInt(yearOfManufacturingFrom) :
                    null;

            Integer tillYear = (yearOfManufacturingTill != null && !yearOfManufacturingTill.isEmpty()) ?
                    Integer.parseInt(yearOfManufacturingTill) :
                    null;

            LocalDate fromDate = fromYear != null ? LocalDate.of(fromYear, 1, 1) : null;
            LocalDate tillDate = tillYear != null ? LocalDate.of(tillYear, 12, 31) : null;

            Predicate predicate = criteriaBuilder.conjunction();

            if (fromDate != null && tillDate != null) {
                predicate = criteriaBuilder.between(
                        root.get("manufacturingDate"),
                        fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                        tillDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
                );
            } else if (fromDate != null) {
                predicate = criteriaBuilder.greaterThanOrEqualTo(
                        root.get("manufacturingDate"),
                        fromDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
                );
            } else if (tillDate != null) {
                predicate = criteriaBuilder.lessThanOrEqualTo(
                        root.get("manufacturingDate"),
                        tillDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
                );
            }
            return predicate;
        };
    }
}
