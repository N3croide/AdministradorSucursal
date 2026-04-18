package com.accenture.co.controller;

import org.springframework.web.bind.annotation.RestController;

import com.accenture.co.application.dto.FranchiseRequest;
import com.accenture.co.application.dto.FranchiseResponse;
import com.accenture.co.service.FranchiseService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController()
@RequestMapping("/franchise")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseService franchiseService;

    @PostMapping("/save")
    public Mono<FranchiseResponse> saveFranchise(@RequestBody FranchiseRequest data) {
        return this.franchiseService.saveFranchise(data);
    }

    @GetMapping("/getAll")
    public Flux<FranchiseResponse> getAllFranchises() {
        return this.franchiseService.getAll();
    }

    @PutMapping("update/{id}")
        public Mono<FranchiseResponse> updateFranchise(@PathVariable Long id, @RequestBody FranchiseRequest dto) {
            return this.franchiseService.updateFranchise(id, dto);
        }
}
