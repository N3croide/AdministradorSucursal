package com.accenture.co.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.BranchRequest;
import com.accenture.co.application.dto.BranchResponse;
import com.accenture.co.application.mapper.BranchMapper;
import com.accenture.co.repository.BranchProductRepository;
import com.accenture.co.repository.BranchRespository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * BranchService
 */
@RequiredArgsConstructor
@Service
public class BranchService {

    private final BranchRespository branchRespository;
    private final BranchMapper branchMapper;
    private final BranchProductRepository branchProductRepository;

    public Mono<BranchResponse> saveBranch(BranchRequest dto) {
        return Mono.just(dto).map(this.branchMapper::toEntity).flatMap(this.branchRespository::save)
                .map(this.branchMapper::toResponse).onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
                    System.err.println("Error al guardar branch: " + e.getMessage());
                    return Mono.error(new Exception("Error:"));
                });
    }

    public Mono<BranchResponse> updateBranch(Long id, BranchRequest dto) {
        return this.branchRespository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(b -> {
                    this.branchMapper.updateEntityFromRequest(dto, b);
                    return b;
                }).flatMap(this.branchRespository::save).map(this.branchMapper::toResponse).onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
                    System.err.println("Error al actualizar branch: " + e.getMessage());
                    return Mono.error(new Exception("Error:"));
                });

    }

    public Flux<BranchResponse> getAll() {
        return this.branchRespository.findAll().map(this.branchMapper::toResponse).onErrorResume(e -> {
            if (e instanceof ResponseStatusException)
                return Mono.error(e);
            System.err.println("Error al obtener todas las branches: " + e.getMessage());
            return Mono.error(new Exception("Error:"));
        });
    }

    public Mono<BranchProductResponse> getAllProducts(Long branchId) {
        return this.branchRespository.findById(branchId)
                .switchIfEmpty(
                        Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada")))
                .flatMap(branch -> this.branchProductRepository.findProductsByBranchId(branch.getId())
                        .collectList()
                        .map(productList -> {
                            BranchProductResponse response = new BranchProductResponse();
                            response.setBranchId(branch.getId());
                            response.setBranchName(branch.getName());
                            response.setProducts(productList);
                            return response;
                        }))
                .onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
                });
    }
}
