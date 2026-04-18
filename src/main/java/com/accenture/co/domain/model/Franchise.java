package com.accenture.co.domain.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Franchise
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("franchises")
public class Franchise {

    @Id
    private Long id;
    private String name;

    @Transient
    private List<Branch> branches;    
}
