package com.accenture.co.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.BranchProductRequest;
import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.mapper.BranchProductMapper;
import com.accenture.co.repository.BranchProductRepository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class BranchProductService {

    private final BranchProductRepository  branchProductRespository;
    private final BranchProductMapper branchProductMapper;

    public Mono<BranchProductResponse> saveBranchProduct(BranchProductRequest dto) {
        return Mono.just(dto).map(this.branchProductMapper::toEntity)
                .flatMap(this.branchProductRespository::save)
                .map(this.branchProductMapper::toResponse);
    }

    public Mono<BranchProductResponse> updateBranchProduct(BranchProductRequest dto) {
        return this.branchProductRespository.findByBranchIdAndProductId(dto.getBranchId(), dto.getProductId())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(bp -> {
                    if(dto.getStock() != bp.getStock()){
                        bp.setStock(dto.getStock());
                    }
                    return bp;
                }).flatMap(this.branchProductRespository::save).map(this.branchProductMapper::toResponse);
    }

    public Flux<BranchProductResponse> getAll() {
        return branchProductRespository.findAll().map(this.branchProductMapper::toResponse);
    }

    public Mono<Void> deleteBranchProduct(Long branchId, Long productId) {
        return this.branchProductRespository.findByBranchIdAndProductId(branchId, productId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(p -> this.branchProductRespository.deleteByBranchIdAndProductId(branchId, productId));
    }

}
