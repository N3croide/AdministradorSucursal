package com.accenture.co.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.accenture.co.application.dto.FranchiseRequest;
import com.accenture.co.application.dto.FranchiseResponse;
import com.accenture.co.domain.model.Franchise;

/**
 * FranchiseMapper
 */
@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface FranchiseMapper {

    FranchiseResponse toResponse(Franchise franchise);

    Franchise toEntity(FranchiseRequest franchise);

    void updateEntityFromRequest(FranchiseRequest request, @MappingTarget Franchise entity);

}
