package com.accenture.co.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.accenture.co.application.dto.BranchProductRequest;
import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.domain.model.BranchProduct;

@Mapper(componentModel = "spring")
public interface BranchProductMapper {

    BranchProductResponse toResponse(BranchProduct branchProduct);

    BranchProduct toEntity(BranchProductRequest branchProduct);

    void updateEntityFromRequest(BranchProductRequest request, @MappingTarget BranchProduct entity);

}
