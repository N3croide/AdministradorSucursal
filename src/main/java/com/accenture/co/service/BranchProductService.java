package com.accenture.co.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.BranchProductRequest;
import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.ProductInBranch;
import com.accenture.co.application.mapper.BranchProductMapper;
import com.accenture.co.domain.model.Branch;
import com.accenture.co.domain.model.BranchProduct;
import com.accenture.co.repository.BranchProductRepository;
import com.accenture.co.repository.BranchRespository;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class BranchProductService {

    private final BranchProductRepository branchProductRespository;
    private final BranchProductMapper branchProductMapper;
    private final BranchRespository branchRespository;

    public Mono<BranchProductResponse> saveBranchProduct(BranchProductRequest dto) {
        return Mono.just(dto).map(this.branchProductMapper::toEntity)
                .flatMap(this.branchProductRespository::save)
                .flatMap(saved -> {
                    return this.branchProductRespository.findProductsByBranchId(dto.getBranchId()).collectList()
                            .map(products -> {
                                BranchProductResponse bpR = this.branchProductMapper.toResponse(saved, products);
                                return bpR;
                            });
                }).onErrorResume(e -> {
                    System.err.println("Error al asociar producto: " + e.getMessage());
                    return Mono.error(new Exception("Error:"));
                });
    }

    public Mono<BranchProductResponse> updateBranchProduct(BranchProductRequest dto) {
        return this.branchProductRespository.findByBranchIdAndProductId(dto.getBranchId(), dto.getProductId())
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No asociado")))
                .flatMap((BranchProduct existingEntity) -> {
                    existingEntity.setStock(dto.getStock());
                    return this.branchProductRespository.update(existingEntity)
                            .thenReturn(existingEntity);
                })
                .flatMap(updatedEntity -> Mono.zip(
                        this.branchRespository.findById(updatedEntity.getBranchId()),
                        this.branchProductRespository.findProductsByBranchId(updatedEntity.getBranchId(), updatedEntity.getProductId()).collectList())
                        .map(tuple -> {
                            Branch branch = tuple.getT1();
                            List<ProductInBranch> productList = tuple.getT2();

                            BranchProductResponse response = this.branchProductMapper.toResponse(branch, productList);
                            return response;
                        }))
                .onErrorResume(e -> {
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
                });
    }

    public Flux<BranchProductResponse> getAll() {
        return branchProductRespository.findAll().map(this.branchProductMapper::toResponse).onErrorResume(e -> {
            System.err.println("Error al obtener todos los registro de las sucursal - productos : " + e.getMessage());
            return Mono.error(new Exception("Error:"));
        });
    }

    public Mono<Void> deleteBranchProduct(Long branchId, Long productId) {
        return this.branchProductRespository.findByBranchIdAndProductId(branchId, productId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(p -> this.branchProductRespository.deleteByBranchIdAndProductId(branchId, productId))
                .onErrorResume(e -> {
                    System.err.println("Error al borrar de la sucursal - productos : " + e.getMessage());
                    return Mono.error(new Exception("Error:"));
                });
    }

}
