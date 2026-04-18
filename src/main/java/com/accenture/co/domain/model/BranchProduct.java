package com.accenture.co.domain.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("branch_products")
public class BranchProduct {

    private Long branchId;
    private Long productId;

    private Integer stock;
}
