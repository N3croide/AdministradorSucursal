package com.accenture.co.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.ProductRequest;
import com.accenture.co.application.dto.ProductResponse;
import com.accenture.co.application.mapper.ProductMapper;
import com.accenture.co.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRespository;
    private final ProductMapper productMapper;

    public Mono<ProductResponse> saveProduct(ProductRequest dto) {
        return Mono.just(dto).map(this.productMapper::toEntity)
                .flatMap(this.productRespository::save)
                .map(this.productMapper::toResponse);
    }

    public Mono<ProductResponse> updateProduct(Long id, ProductRequest dto) {
        return this.productRespository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(product -> {
                    this.productMapper.updateEntityFromRequest(dto, product);
                    return product;
                }).flatMap(this.productRespository::save).map(this.productMapper::toResponse);
    }

    public Flux<ProductResponse> getAll() {
        return productRespository.findAll().map(this.productMapper::toResponse);
    }

    public Mono<Void> deleteProduct(Long id) {
        return this.productRespository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(p -> this.productRespository.deleteById(id));
    }

}
