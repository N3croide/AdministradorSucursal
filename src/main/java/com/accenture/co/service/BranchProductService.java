package com.accenture.co.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.BranchProductRequest;
import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.mapper.BranchProductMapper;
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
                            .map(products -> this.branchProductMapper.toResponse(saved, products));
                }).onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
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
                        this.branchProductRespository
                                .findProductsByBranchId(updatedEntity.getBranchId(), updatedEntity.getProductId())
                                .collectList())
                        .map(tuple -> this.branchProductMapper.toResponse(tuple.getT1(), tuple.getT2())))
                .onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
                    System.err.println("Error al actualizar producto de sucursal: " + e.getMessage());
                    return Mono.error(new Exception("Error:"));
                });
    }

    public Flux<BranchProductResponse> getAll() {
        return branchProductRespository.findAll().map(this.branchProductMapper::toResponse).onErrorResume(e -> {
            if (e instanceof ResponseStatusException)
                return Mono.error(e);
            System.err.println("Error al obtener todos los registro de las sucursal - productos : " + e.getMessage());
            return Mono.error(new Exception("Error:"));
        });
    }

    public Mono<Boolean> deleteBranchProduct(Long branchId, Long productId) {
        System.out.println(branchId + " - " + productId);
        return this.branchProductRespository.deleteByBranchIdAndProductId(branchId, productId)
                .map(rowsDeleted -> {
                    if (rowsDeleted == 0) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No se encontro ese producto en dicha sucursal");
                    }
                    return true;
                })
                .onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
                    if (e instanceof ResponseStatusException) {
                        return Mono.error(e);
                    }

                    e.printStackTrace();
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error interno: " + e.getMessage()));
                });
    }

}
