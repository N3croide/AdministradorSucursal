package com.accenture.co.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchProductRequest {

    private Long branchId;
    private Long productId;
    private Integer stock;
}
