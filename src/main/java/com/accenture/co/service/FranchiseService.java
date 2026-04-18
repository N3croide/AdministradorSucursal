package com.accenture.co.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.FranchiseRequest;
import com.accenture.co.application.dto.FranchiseResponse;
import com.accenture.co.application.mapper.FranchiseMapper;
import com.accenture.co.repository.FranchiseRespository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * FranchiseService
 */
@Service
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRespository franchiseRespository;
    private final FranchiseMapper franchiseMapper;

    public Mono<FranchiseResponse> saveFranchise(FranchiseRequest dto) {
        return Mono.just(dto).map(this.franchiseMapper::toEntity)
                .flatMap(this.franchiseRespository::save)
                .map(this.franchiseMapper::toResponse);
    }

    public Mono<FranchiseResponse> updateFranchise(Long id, FranchiseRequest dto) {
        return this.franchiseRespository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(franchise -> {
                    this.franchiseMapper.updateEntityFromRequest(dto, franchise);
                    return franchise;
                }).flatMap(this.franchiseRespository::save).map(this.franchiseMapper::toResponse);
    }

    public Flux<FranchiseResponse> getAll(){
        return this.franchiseRespository.findAll().map(this.franchiseMapper::toResponse);
    }

}
