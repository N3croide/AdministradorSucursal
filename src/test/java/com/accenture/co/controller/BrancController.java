package com.accenture.co.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.BranchResponse;
import com.accenture.co.service.BranchProductService;
import com.accenture.co.service.BranchService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * BrancController
 */
@WebFluxTest(BranchController.class)
public class BrancController {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BranchService branchService;

    @MockBean
    private BranchProductService branchProductService;

    @Test
    @DisplayName("POST /branch/save - 200 con body correcto")
    void saveBranch_ok() {
        BranchResponse response = new BranchResponse();
        response.setId(1L);
        response.setName("Sucursal Central");

        when(branchService.saveBranch(any())).thenReturn(Mono.just(response));

        webTestClient.post().uri("/branch/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Sucursal Central\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Sucursal Central");
    }

    @Test
    @DisplayName("GET /branch/getAll - 200 con lista de sucursales")
    void getAllBranchs_ok() {
        BranchResponse r1 = new BranchResponse();
        r1.setId(1L);
        BranchResponse r2 = new BranchResponse();
        r2.setId(2L);

        when(branchService.getAll()).thenReturn(Flux.just(r1, r2));

        webTestClient.get().uri("/branch/getAll")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BranchResponse.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("PUT /branch/update/{id} - 200 al actualizar")
    void updateBranch_ok() {
        BranchResponse response = new BranchResponse();
        response.setId(1L);

        when(branchService.updateBranch(anyLong(), any())).thenReturn(Mono.just(response));

        webTestClient.put().uri("/branch/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Nueva\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("PUT /branch/addProduct - 200 al agregar producto")
    void addProduct_ok() {
        BranchProductResponse response = new BranchProductResponse();
        response.setBranchId(1L);

        when(branchProductService.saveBranchProduct(any())).thenReturn(Mono.just(response));

        webTestClient.put().uri("/branch/addProduct")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"branchId\":1,\"productId\":10,\"stock\":5}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("PUT /branch/updateStock - 200 al actualizar stock")
    void updateStock_ok() {
        BranchProductResponse response = new BranchProductResponse();
        response.setBranchId(1L);

        when(branchProductService.updateBranchProduct(any())).thenReturn(Mono.just(response));

        webTestClient.put().uri("/branch/updateStock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"branchId\":1,\"productId\":10,\"stock\":20}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /branch/getAllProducts/{id} - 200 con productos de la sucursal")
    void getAllProducts_ok() {
        BranchProductResponse response = new BranchProductResponse();
        response.setBranchId(1L);

        when(branchService.getAllProducts(1L)).thenReturn(Mono.just(response));

        webTestClient.get().uri("/branch/getAllProducts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BranchProductResponse.class)
                .isEqualTo(response);
    }

    @Test
    @DisplayName("DELETE /branch/deleteProduct/{branchId}/{productId} - 200 al eliminar")
    void deleteProduct_ok() {
        when(branchProductService.deleteBranchProduct(1L, 10L)).thenReturn(Mono.just(true));

        webTestClient.delete().uri("/branch/deleteProduct/1/10")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class)
                .isEqualTo(true);
    }
}
