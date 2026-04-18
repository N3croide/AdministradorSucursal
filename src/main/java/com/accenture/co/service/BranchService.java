package com.accenture.co.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.BranchRequest;
import com.accenture.co.application.dto.BranchResponse;
import com.accenture.co.application.mapper.BranchMapper;
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

    public Mono<BranchResponse> saveBranch(BranchRequest dto) {
        return Mono.just(dto).map(this.branchMapper::toEntity).flatMap(this.branchRespository::save)
                .map(this.branchMapper::toResponse);
    }

    public Mono<BranchResponse> updateBranch(Long id, BranchRequest dto) {
        return this.branchRespository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(b -> {
                    this.branchMapper.updateEntityFromRequest(dto, b);
                    return b;
                }).flatMap(this.branchRespository::save).map(this.branchMapper::toResponse);

    }

    public Flux<BranchResponse> getAll() {
        return this.branchRespository.findAll().map(this.branchMapper::toResponse);
    }
}
