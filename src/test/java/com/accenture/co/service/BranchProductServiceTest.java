package com.accenture.co.service;

import com.accenture.co.application.dto.BranchProductRequest;
import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.mapper.BranchProductMapper;
import com.accenture.co.domain.model.Branch;
import com.accenture.co.domain.model.BranchProduct;
import com.accenture.co.repository.BranchProductRepository;
import com.accenture.co.repository.BranchRespository;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BranchProductServiceTest {

    @Mock
    private BranchProductRepository branchProductRespository;

    @Mock
    private BranchProductMapper branchProductMapper;

    @Mock
    private BranchRespository branchRespository;

    @InjectMocks
    private BranchProductService branchProductService;

    private BranchProductRequest request;
    private BranchProduct branchProduct;
    private BranchProductResponse response;
    private Branch branch;

    @BeforeEach
    void setUp() {
        request = new BranchProductRequest();
        request.setBranchId(1L);
        request.setProductId(10L);
        request.setStock(50);

        branchProduct = new BranchProduct();
        branchProduct.setBranchId(1L);
        branchProduct.setProductId(10L);
        branchProduct.setStock(50);

        branch = new Branch();
        branch.setId(1L);
        branch.setName("Sucursal Central");

        response = new BranchProductResponse();
        response.setBranchId(1L);
        response.setBranchName("Sucursal Central");
    }

    // ───────────────── saveBranchProduct ─────────────────

    @Test
    @DisplayName("saveBranchProduct - guarda y retorna respuesta con productos de la sucursal")
    void saveBranchProduct_success() {
        when(branchProductMapper.toEntity(request)).thenReturn(branchProduct);
        when(branchProductRespository.save(branchProduct)).thenReturn(Mono.just(branchProduct));
        when(branchProductRespository.findProductsByBranchId(1L)).thenReturn(Flux.empty());
        when(branchProductMapper.toResponse(eq(branchProduct), any())).thenReturn(response);

        StepVerifier.create(branchProductService.saveBranchProduct(request))
                .expectNext(response)
                .verifyComplete();

        verify(branchProductRespository).save(branchProduct);
    }

    @Test
    @DisplayName("saveBranchProduct - propaga error cuando falla el guardado")
    void saveBranchProduct_error() {
        when(branchProductMapper.toEntity(request)).thenReturn(branchProduct);
        when(branchProductRespository.save(branchProduct))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(branchProductService.saveBranchProduct(request))
                .expectError(Exception.class)
                .verify();
    }

    // ───────────────── updateBranchProduct ─────────────────

    @Test
    @DisplayName("updateBranchProduct - actualiza stock correctamente")
    void updateBranchProduct_success() {
        when(branchProductRespository.findByBranchIdAndProductId(1L, 10L))
                .thenReturn(Mono.just(branchProduct));
        when(branchProductRespository.update(branchProduct)).thenReturn(Mono.empty());
        when(branchRespository.findById(1L)).thenReturn(Mono.just(branch));
        when(branchProductRespository.findProductsByBranchId(1L, 10L)).thenReturn(Flux.empty());
        when(branchProductMapper.toResponse(eq(branch), any())).thenReturn(response);

        StepVerifier.create(branchProductService.updateBranchProduct(request))
                .expectNext(response)
                .verifyComplete();

        verify(branchProductRespository).update(argThat(bp -> bp.getStock() == 50));
    }

    @Test
    @DisplayName("updateBranchProduct - lanza NOT_FOUND cuando no existe la asociacion")
    void updateBranchProduct_notFound() {
        when(branchProductRespository.findByBranchIdAndProductId(1L, 10L))
                .thenReturn(Mono.empty());

        StepVerifier.create(branchProductService.updateBranchProduct(request))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    // ───────────────── deleteBranchProduct ─────────────────

    @Test
    @DisplayName("deleteBranchProduct - elimina correctamente y retorna true")
    void deleteBranchProduct_success() {
        when(branchProductRespository.deleteByBranchIdAndProductId(1L, 10L))
                .thenReturn(Mono.just(1));

        StepVerifier.create(branchProductService.deleteBranchProduct(1L, 10L))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("deleteBranchProduct - lanza NOT_FOUND cuando no encuentra el registro")
    void deleteBranchProduct_notFound() {
        when(branchProductRespository.deleteByBranchIdAndProductId(1L, 10L))
                .thenReturn(Mono.just(0));

        StepVerifier.create(branchProductService.deleteBranchProduct(1L, 10L))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    @Test
    @DisplayName("deleteBranchProduct - lanza INTERNAL_SERVER_ERROR ante error inesperado")
    void deleteBranchProduct_internalError() {
        when(branchProductRespository.deleteByBranchIdAndProductId(1L, 10L))
                .thenReturn(Mono.error(new RuntimeException("conexion perdida")));

        StepVerifier.create(branchProductService.deleteBranchProduct(1L, 10L))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR)
                .verify();
    }

    // ───────────────── getAll ─────────────────

    @Test
    @DisplayName("getAll - retorna todos los registros branch-producto")
    void getAll_success() {
        BranchProductResponse r2 = new BranchProductResponse();
        r2.setBranchId(2L);

        when(branchProductRespository.findAll()).thenReturn(Flux.just(branchProduct));
        when(branchProductMapper.toResponse(branchProduct)).thenReturn(response);

        StepVerifier.create(branchProductService.getAll())
                .expectNext(response)
                .verifyComplete();
    }
}
