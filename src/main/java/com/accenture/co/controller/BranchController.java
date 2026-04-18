package com.accenture.co.controller;

import org.springframework.web.bind.annotation.RestController;

import com.accenture.co.application.dto.BranchProductRequest;
import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.BranchRequest;
import com.accenture.co.application.dto.BranchResponse;
import com.accenture.co.service.BranchProductService;
import com.accenture.co.service.BranchService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController()
@RequestMapping("/branch")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;
    private final BranchProductService branchProductService;

    @PostMapping("/save")
    public Mono<BranchResponse> saveBranch(@RequestBody BranchRequest dto) {
        return this.branchService.saveBranch(dto);
    }

    @GetMapping("/getAll")
    public Flux<BranchResponse> getAllBranchs() {
        return this.branchService.getAll();
    }

    @PutMapping("/update/{id}")
    public Mono<BranchResponse> updateBranch(@PathVariable Long id, @RequestBody BranchRequest dto) {
        return this.branchService.updateBranch(id, dto);
    }

    @PutMapping("/addProduct")
    public Mono<BranchProductResponse> addProduct(@RequestBody BranchProductRequest dto){
        return this.branchProductService.saveBranchProduct(dto);
    }

    @PutMapping("/updateStock")
    public Mono<BranchProductResponse> updateStock(@RequestBody BranchProductRequest dto){
        return this.branchProductService.updateBranchProduct(dto);
    }

    @GetMapping("/getAllProducts/{id}")
        public Mono<BranchProductResponse> getAllProducts(@PathVariable Long id ) {
            return this.branchService.getAllProducts(id);
        }
        
}
