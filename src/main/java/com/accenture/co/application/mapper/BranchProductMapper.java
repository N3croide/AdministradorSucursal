package com.accenture.co.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.accenture.co.application.dto.BranchProductRequest;
import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.ProductInBranch;
import com.accenture.co.domain.model.Branch;
import com.accenture.co.domain.model.BranchProduct;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = ProductMapper.class)
public interface BranchProductMapper {

    BranchProductResponse toResponse(BranchProduct branchProduct);


    BranchProductResponse toResponse(BranchProduct branchProduct, List<ProductInBranch> products);

    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "products", source = "products")
    BranchProductResponse toResponse(Branch branch, List<ProductInBranch> products);

    BranchProduct toEntity(BranchProductRequest branchProduct);

    void updateEntityFromRequest(BranchProductRequest request, @MappingTarget BranchProduct entity);

}
