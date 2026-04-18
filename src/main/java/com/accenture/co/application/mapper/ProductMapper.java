package com.accenture.co.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.accenture.co.application.dto.ProductRequest;
import com.accenture.co.application.dto.ProductResponse;
import com.accenture.co.domain.model.Product;

/**
 * ProductMapper
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    Product toEntity(ProductRequest product);

    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product entity);
}
