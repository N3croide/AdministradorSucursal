package com.accenture.co.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.accenture.co.application.dto.FranchiseResponse;
import com.accenture.co.service.FranchiseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest(FranchiseController.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FranchiseService franchiseService;

    @Test
    @DisplayName("POST /franchise/save - 200 con body correcto")
    void saveFranchise_ok() {
        FranchiseResponse response = new FranchiseResponse();
        response.setId(1L);
        response.setName("Franquicia Test");

        when(franchiseService.saveFranchise(any())).thenReturn(Mono.just(response));

        webTestClient.post().uri("/franchise/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Franquicia Test\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(FranchiseResponse.class)
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET /franchise/getAll - 200 con lista de franquicias")
    void getAllFranchises_ok() {
        FranchiseResponse r1 = new FranchiseResponse();
        r1.setId(1L);

        when(franchiseService.getAll()).thenReturn(Flux.just(r1));

        webTestClient.get().uri("/franchise/getAll")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(FranchiseResponse.class)
                .hasSize(1);
    }

    @Test
    @DisplayName("PUT /franchise/update/{id} - 200 al actualizar")
    void updateFranchise_ok() {
        FranchiseResponse response = new FranchiseResponse();
        response.setId(1L);

        when(franchiseService.updateFranchise(anyLong(), any())).thenReturn(Mono.just(response));

        webTestClient.put().uri("/franchise/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Nueva Franquicia\"}")
                .exchange()
                .expectStatus().isOk();
    }
}



