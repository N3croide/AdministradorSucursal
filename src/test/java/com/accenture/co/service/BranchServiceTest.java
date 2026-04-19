package com.accenture.co.service;

import com.accenture.co.application.dto.BranchRequest;
import com.accenture.co.application.dto.BranchResponse;
import com.accenture.co.application.mapper.BranchMapper;
import com.accenture.co.domain.model.Branch;
import com.accenture.co.repository.BranchProductRepository;
import com.accenture.co.repository.BranchRespository;
import com.accenture.co.service.BranchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @Mock
    private BranchRespository branchRespository;

    @Mock
    private BranchMapper branchMapper;

    @Mock
    private BranchProductRepository branchProductRepository;

    @InjectMocks
    private BranchService branchService;

    private Branch branch;
    private BranchRequest branchRequest;
    private BranchResponse branchResponse;

    @BeforeEach
    void setUp() {
        branch = new Branch();
        branch.setId(1L);
        branch.setName("Sucursal Central");

        branchRequest = new BranchRequest();
        branchRequest.setName("Sucursal Central");

        branchResponse = new BranchResponse();
        branchResponse.setId(1L);
        branchResponse.setName("Sucursal Central");
    }

    // ───────────────── saveBranch ─────────────────

    @Test
    @DisplayName("saveBranch - guarda correctamente y retorna respuesta")
    void saveBranch_success() {
        when(branchMapper.toEntity(branchRequest)).thenReturn(branch);
        when(branchRespository.save(branch)).thenReturn(Mono.just(branch));
        when(branchMapper.toResponse(branch)).thenReturn(branchResponse);

        StepVerifier.create(branchService.saveBranch(branchRequest))
                .expectNext(branchResponse)
                .verifyComplete();

        verify(branchRespository).save(branch);
    }

    @Test
    @DisplayName("saveBranch - propaga error cuando el repositorio falla")
    void saveBranch_repositoryError() {
        when(branchMapper.toEntity(branchRequest)).thenReturn(branch);
        when(branchRespository.save(branch)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(branchService.saveBranch(branchRequest))
                .expectError(Exception.class)
                .verify();
    }

    // ───────────────── updateBranch ─────────────────

    @Test
    @DisplayName("updateBranch - actualiza correctamente cuando la sucursal existe")
    void updateBranch_success() {
        when(branchRespository.findById(1L)).thenReturn(Mono.just(branch));
        doNothing().when(branchMapper).updateEntityFromRequest(branchRequest, branch);
        when(branchRespository.save(branch)).thenReturn(Mono.just(branch));
        when(branchMapper.toResponse(branch)).thenReturn(branchResponse);

        StepVerifier.create(branchService.updateBranch(1L, branchRequest))
                .expectNext(branchResponse)
                .verifyComplete();

        verify(branchMapper).updateEntityFromRequest(branchRequest, branch);
    }

    @Test
    @DisplayName("updateBranch - lanza NOT_FOUND cuando la sucursal no existe")
    void updateBranch_notFound() {
        when(branchRespository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(branchService.updateBranch(99L, branchRequest))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    // ───────────────── getAll ─────────────────

    @Test
    @DisplayName("getAll - retorna todas las sucursales")
    void getAll_success() {
        Branch branch2 = new Branch();
        branch2.setId(2L);
        branch2.setName("Sucursal Norte");
        BranchResponse response2 = new BranchResponse();
        response2.setId(2L);

        when(branchRespository.findAll()).thenReturn(Flux.just(branch, branch2));
        when(branchMapper.toResponse(branch)).thenReturn(branchResponse);
        when(branchMapper.toResponse(branch2)).thenReturn(response2);

        StepVerifier.create(branchService.getAll())
                .expectNext(branchResponse)
                .expectNext(response2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getAll - retorna vacío cuando no hay sucursales")
    void getAll_empty() {
        when(branchRespository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(branchService.getAll())
                .verifyComplete();
    }

    // ───────────────── getAllProducts ─────────────────

    @Test
    @DisplayName("getAllProducts - retorna productos de una sucursal existente")
    void getAllProducts_success() {
        Object product = new Object(); // usa tu tipo real de ProductResponse
        when(branchRespository.findById(1L)).thenReturn(Mono.just(branch));
        when(branchProductRepository.findProductsByBranchId(1L)).thenReturn(Flux.empty());

        StepVerifier.create(branchService.getAllProducts(1L))
                .assertNext(response -> {
                    assert response.getBranchId().equals(1L);
                    assert response.getBranchName().equals("Sucursal Central");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("getAllProducts - lanza NOT_FOUND cuando sucursal no existe")
    void getAllProducts_notFound() {
        when(branchRespository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(branchService.getAllProducts(99L))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }
}
