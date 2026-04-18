package com.accenture.co.application.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchProductResponse {

    private Long branchId;
    private String branchName;
    private List<ProductInBranch> products;
}
