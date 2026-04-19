package com.accenture.co.service;

import com.accenture.co.application.dto.FranchiseRequest;
import com.accenture.co.application.dto.FranchiseResponse;
import com.accenture.co.application.mapper.FranchiseMapper;
import com.accenture.co.domain.model.Branch;
import com.accenture.co.domain.model.Franchise;
import com.accenture.co.repository.BranchRespository;
import com.accenture.co.repository.FranchiseRespository;

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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRespository franchiseRepository;

    @Mock
    private FranchiseMapper franchiseMapper;

    @Mock
    private BranchRespository branchRespository;

    @InjectMocks
    private FranchiseService franchiseService;

    private Franchise franchise;
    private FranchiseRequest franchiseRequest;
    private FranchiseResponse franchiseResponse;

    @BeforeEach
    void setUp() {
        franchise = new Franchise();
        franchise.setId(1L);
        franchise.setName("Franquicia Test");
        franchise.setBranches(new ArrayList<>());

        franchiseRequest = new FranchiseRequest();
        franchiseRequest.setName("Franquicia Test");

        franchiseResponse = new FranchiseResponse();
        franchiseResponse.setId(1L);
        franchiseResponse.setName("Franquicia Test");
    }

    // ───────────────── saveFranchise ─────────────────

    @Test
    @DisplayName("saveFranchise - guarda franquicia sin sucursales correctamente")
    void saveFranchise_noBranches_success() {
        when(franchiseMapper.toEntity(franchiseRequest)).thenReturn(franchise);
        when(franchiseRepository.save(franchise)).thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        StepVerifier.create(franchiseService.saveFranchise(franchiseRequest))
                .expectNext(franchiseResponse)
                .verifyComplete();

        verify(franchiseRepository).save(franchise);
        verify(branchRespository, never()).saveAll(any(Iterable.class));
    }

    @Test
    @DisplayName("saveFranchise - guarda franquicia con sucursales y las persiste")
    void saveFranchise_withBranches_success() {
        Branch branch1 = new Branch();
        branch1.setName("Sucursal A");
        Branch branch2 = new Branch();
        branch2.setName("Sucursal B");
        franchise.setBranches(List.of(branch1, branch2));

        when(franchiseMapper.toEntity(franchiseRequest)).thenReturn(franchise);
        when(franchiseRepository.save(franchise)).thenReturn(Mono.just(franchise));
        when(branchRespository.saveAll(any(Iterable.class))).thenReturn(Flux.just(branch1, branch2));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        StepVerifier.create(franchiseService.saveFranchise(franchiseRequest))
                .expectNext(franchiseResponse)
                .verifyComplete();

        verify(branchRespository).saveAll(any(Iterable.class));
    }

    @Test
    @DisplayName("saveFranchise - propaga error cuando el repositorio falla")
    void saveFranchise_repositoryError() {
        when(franchiseMapper.toEntity(franchiseRequest)).thenReturn(franchise);
        when(franchiseRepository.save(franchise))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(franchiseService.saveFranchise(franchiseRequest))
                .expectError(Exception.class)
                .verify();
    }

    // ───────────────── updateFranchise ─────────────────

    @Test
    @DisplayName("updateFranchise - actualiza correctamente cuando la franquicia existe")
    void updateFranchise_success() {
        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(franchise));
        doNothing().when(franchiseMapper).updateEntityFromRequest(franchiseRequest, franchise);
        when(franchiseRepository.save(franchise)).thenReturn(Mono.just(franchise));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);

        StepVerifier.create(franchiseService.updateFranchise(1L, franchiseRequest))
                .expectNext(franchiseResponse)
                .verifyComplete();

        verify(franchiseMapper).updateEntityFromRequest(franchiseRequest, franchise);
    }

    @Test
    @DisplayName("updateFranchise - lanza NOT_FOUND cuando la franquicia no existe")
    void updateFranchise_notFound() {
        when(franchiseRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseService.updateFranchise(99L, franchiseRequest))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    // ───────────────── getAll ─────────────────

    @Test
    @DisplayName("getAll - retorna todas las franquicias")
    void getAll_success() {
        Franchise franchise2 = new Franchise();
        franchise2.setId(2L);
        FranchiseResponse response2 = new FranchiseResponse();
        response2.setId(2L);

        when(franchiseRepository.findAll()).thenReturn(Flux.just(franchise, franchise2));
        when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);
        when(franchiseMapper.toResponse(franchise2)).thenReturn(response2);

        StepVerifier.create(franchiseService.getAll())
                .expectNext(franchiseResponse)
                .expectNext(response2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getAll - retorna vacío cuando no hay franquicias")
    void getAll_empty() {
        when(franchiseRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(franchiseService.getAll())
                .verifyComplete();
    }
}
