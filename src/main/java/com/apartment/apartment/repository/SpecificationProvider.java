package com.apartment.apartment.repository;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider<T> {
    Specification<T> getSpecification(String[] params);

    String getKey();
}
