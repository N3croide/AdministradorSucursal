package com.accenture.co.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.accenture.co.application.dto.FranchiseRequest;
import com.accenture.co.application.dto.FranchiseResponse;
import com.accenture.co.application.mapper.FranchiseMapper;
import com.accenture.co.domain.model.Branch;
import com.accenture.co.domain.model.Franchise;
import com.accenture.co.repository.BranchRespository;
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

    private final FranchiseRespository franchiseRepository;
    private final FranchiseMapper franchiseMapper;
    private final BranchRespository branchRespository;

    public Mono<FranchiseResponse> saveFranchise(FranchiseRequest dto) {
        return Mono.just(dto).map(this.franchiseMapper::toEntity)
                .flatMap(franchise -> this.saveFranchiseWithBranches(franchise))
                .map(this.franchiseMapper::toResponse).onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
                    System.err.println("Error al guardar franchise: " + e.getMessage());
                    return Mono.error(new Exception());
                });

    }

    public Mono<FranchiseResponse> updateFranchise(Long id, FranchiseRequest dto) {
        return this.franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(franchise -> {
                    this.franchiseMapper.updateEntityFromRequest(dto, franchise);
                    return franchise;
                }).flatMap(this.franchiseRepository::save).map(this.franchiseMapper::toResponse).onErrorResume(e -> {
                    if (e instanceof ResponseStatusException)
                        return Mono.error(e);
                    System.err.println("Error al actualizar franchise: " + e.getMessage());
                    return Mono.error(new Exception("Error:"));
                });

    }

    public Flux<FranchiseResponse> getAll() {
        return this.franchiseRepository.findAll().map(this.franchiseMapper::toResponse).onErrorResume(e -> {
            if (e instanceof ResponseStatusException)
                return Mono.error(e);
            System.err.println("Error al obtener todas las franchise: " + e.getMessage());
            return Mono.error(new Exception("Error:"));
        });

    }

    // Metodo auxiliar para manejar el registro de una Franchise con la lista de
    // Branches.
    private Mono<Franchise> saveFranchiseWithBranches(Franchise franchise) {
        return franchiseRepository.save(franchise)
                .flatMap(savedFranchise -> {

                    if (franchise.getBranches() == null || franchise.getBranches().isEmpty()) {
                        return Mono.just(savedFranchise);
                    }

                    List<Branch> branchesToSave = franchise.getBranches().stream()
                            .map(branch -> {
                                branch.setFranchiseId(savedFranchise.getId());
                                return branch;
                            })
                            .collect(Collectors.toList());

                    return this.branchRespository.saveAll(branchesToSave)
                            .collectList()
                            .map(savedBranches -> {
                                savedFranchise.setBranches(savedBranches);
                                return savedFranchise;
                            });
                });
    }
}
