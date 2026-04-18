
package com.accenture.co.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.accenture.co.application.dto.BranchRequest;
import com.accenture.co.application.dto.BranchResponse;
import com.accenture.co.domain.model.Branch;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BranchMapper {

    BranchResponse toResponse(Branch branch);

    Branch toEntity(BranchRequest branch);

    void updateEntityFromRequest(BranchRequest dto, @MappingTarget Branch entitry);
}
