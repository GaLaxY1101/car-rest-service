package com.foxminded.korniichyk.car_rest_service.service.contract;

import com.foxminded.korniichyk.car_rest_service.model.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudService<E, ID> {

    void save(E entity);
    void delete(Long id);
    E findById(ID id);
    Page<E> findAll(Pageable pageable);
}
