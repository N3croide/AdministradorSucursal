package com.accenture.co.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.accenture.co.application.dto.ProductInBranch;
import com.accenture.co.domain.model.BranchProduct;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BranchProductRepository extends ReactiveCrudRepository<BranchProduct, Long> {

    Mono<BranchProduct> findByBranchIdAndProductId(Long branchId, Long productId);

    @Modifying
    @Query("DELETE FROM branch_products WHERE branch_id = :branchId AND product_id = :productId")
    Mono<Integer> deleteByBranchIdAndProductId(@Param("branchId") Long branchId, @Param("productId") Long productId);

    @Query("SELECT p.id, p.name, p.price, bp.stock " +
            "FROM products p " +
            "INNER JOIN branch_products bp ON p.id = bp.product_id " +
            "WHERE bp.branch_id = :branchId")
    Flux<ProductInBranch> findProductsByBranchId(Long branchId);

    @Query("SELECT p.id, p.name, p.price, bp.stock " +
            "FROM products p " +
            "INNER JOIN branch_products bp ON p.id = bp.product_id " +
            "WHERE bp.branch_id = :branchId and bp.product_id = :productId")
    Flux<ProductInBranch> findProductsByBranchId(Long branchId, Long productId);

    @Modifying
    @Query("UPDATE branch_products SET stock = :#{#bp.stock} WHERE branch_id = :#{#bp.branchId} AND product_id = :#{#bp.productId}")
    Mono<Void> update(@Param("bp") BranchProduct bp);
}
