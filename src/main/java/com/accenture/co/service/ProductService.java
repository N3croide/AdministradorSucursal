package com.accenture.co.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.ProductRequest;
import com.accenture.co.application.dto.ProductResponse;
import com.accenture.co.application.mapper.BranchProductMapper;
import com.accenture.co.application.mapper.ProductMapper;
import com.accenture.co.repository.BranchRespository;
import com.accenture.co.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BranchRespository branchRepository;
    private final BranchProductMapper branchProductMapper;

    public Mono<ProductResponse> saveProduct(ProductRequest dto) {
        return Mono.just(dto).map(this.productMapper::toEntity)
                .flatMap(this.productRepository::save)
                .map(this.productMapper::toResponse);
    }

    public Mono<ProductResponse> updateProduct(Long id, ProductRequest dto) {
        return this.productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(product -> {
                    this.productMapper.updateEntityFromRequest(dto, product);
                    return product;
                }).flatMap(this.productRepository::save).map(this.productMapper::toResponse);
    }

    public Flux<ProductResponse> getAll() {
        return productRepository.findAll().map(this.productMapper::toResponse);
    }

    public Mono<Void> deleteProduct(Long id) {
        return this.productRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(p -> this.productRepository.deleteById(id));
    }
    
    // INFO: Esto me va a retornar siempre todas las sucursales así no tengan producto, podría devolver solo las que tienen para no saturar;
    public Flux<BranchProductResponse> getMostPopular() {
        return this.branchRepository.findAll()
                .flatMap(branch -> this.productRepository.findMostPopularByBranchId(branch.getId())
                        .map(List::of)
                        .defaultIfEmpty(List.of())
                        .map(productList -> this.branchProductMapper.toResponse(branch, productList)));
    }

}
