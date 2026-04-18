package com.accenture.co.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.accenture.co.application.dto.ProductInBranch;
import com.accenture.co.domain.model.Product;

import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    @Query("SELECT p.id, p.name, p.price, bp.stock " +
            "FROM products p " +
            "JOIN branch_products bp ON p.id = bp.product_id " +
            "WHERE bp.branch_id = :branchId " +
            "ORDER BY bp.stock DESC LIMIT 1")
    Mono<ProductInBranch> findMostPopularByBranchId(Long branchId);
}
