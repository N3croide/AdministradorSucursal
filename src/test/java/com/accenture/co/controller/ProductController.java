package com.accenture.co.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.ProductResponse;
import com.accenture.co.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;



@WebFluxTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("POST /product/save - 200 con body correcto")
    void saveProduct_ok() {
        ProductResponse response = new ProductResponse();
        response.setId(1L);
        response.setName("Producto Test");

        when(productService.saveProduct(any())).thenReturn(Mono.just(response));

        webTestClient.post().uri("/product/save")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Producto Test\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ProductResponse.class)
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET /product/getAll - 200 con lista de productos")
    void getAllProducts_ok() {
        ProductResponse r1 = new ProductResponse();
        r1.setId(1L);
        ProductResponse r2 = new ProductResponse();
        r2.setId(2L);

        when(productService.getAll()).thenReturn(Flux.just(r1, r2));

        webTestClient.get().uri("/product/getAll")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductResponse.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("PUT /product/update/{id} - 200 al actualizar")
    void updateProduct_ok() {
        ProductResponse response = new ProductResponse();
        response.setId(1L);

        when(productService.updateProduct(anyLong(), any())).thenReturn(Mono.just(response));

        webTestClient.put().uri("/product/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Actualizado\"}")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("GET /product/popular - 200 con productos populares por sucursal")
    void getMostPopular_ok() {
        BranchProductResponse bpr = new BranchProductResponse();
        bpr.setBranchId(1L);

        when(productService.getMostPopular()).thenReturn(Flux.just(bpr));

        webTestClient.get().uri("/product/popular")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BranchProductResponse.class)
                .hasSize(1);
    }
}
