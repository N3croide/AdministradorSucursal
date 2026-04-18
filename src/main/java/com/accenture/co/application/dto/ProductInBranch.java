package com.accenture.co.application.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductInBranch {

    private Long id;
    private String name;
    private BigDecimal price;
    private Long stock;
    
}
