package com.accenture.co.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.accenture.co.domain.model.BranchProduct;

import reactor.core.publisher.Mono;

@Repository
public interface BranchProductRepository extends ReactiveCrudRepository<BranchProduct, Long> {

    Mono<BranchProduct> findByBranchIdAndProductId(Long branchId, Long productId);

    Mono<Void> deleteByBranchIdAndProductId(Long branchId, Long productId);

}
