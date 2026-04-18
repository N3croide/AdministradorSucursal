package com.accenture.co.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Branch
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("branches")
public class Branch {

    @Id
    private Long id;
    private String name;
    private String address;
    private Long franchiseId;
}
