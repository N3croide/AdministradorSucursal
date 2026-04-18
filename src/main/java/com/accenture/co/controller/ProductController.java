package com.accenture.co.controller;

import org.springframework.web.bind.annotation.RestController;

import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.ProductRequest;
import com.accenture.co.application.dto.ProductResponse;
import com.accenture.co.service.ProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/save")
    public Mono<ProductResponse> saveProduct(@RequestBody ProductRequest data) {
        return this.productService.saveProduct(data);
    }

    @GetMapping("/getAll")
    public Flux<ProductResponse> getAllProducts() {
        return this.productService.getAll();
    }

    @PutMapping("/update/{id}")
    public Mono<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest){
        return this.productService.updateProduct(id, productRequest);
    }

    @GetMapping("/popular")
    public Flux<BranchProductResponse> getMostPopular(){
        return this.productService.getMostPopular();
    }
}
