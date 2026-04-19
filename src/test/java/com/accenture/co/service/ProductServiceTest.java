package com.accenture.co.service;

import com.accenture.co.application.dto.BranchProductResponse;
import com.accenture.co.application.dto.ProductInBranch;
import com.accenture.co.application.dto.ProductRequest;
import com.accenture.co.application.dto.ProductResponse;
import com.accenture.co.application.mapper.BranchProductMapper;
import com.accenture.co.application.mapper.ProductMapper;
import com.accenture.co.domain.model.Branch;
import com.accenture.co.domain.model.Product;
import com.accenture.co.repository.BranchRespository;
import com.accenture.co.repository.ProductRepository;

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

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private BranchRespository branchRepository;

    @Mock
    private BranchProductMapper branchProductMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private ProductInBranch productInBranch = ProductInBranch.builder().id(1L).name("TEST").stock(10L).build();

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Producto Test");

        productRequest = new ProductRequest();
        productRequest.setName("Producto Test");

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Producto Test");
    }

    // ───────────────── saveProduct ─────────────────

    @Test
    @DisplayName("saveProduct - guarda y retorna respuesta correctamente")
    void saveProduct_success() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        StepVerifier.create(productService.saveProduct(productRequest))
                .expectNext(productResponse)
                .verifyComplete();

        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("saveProduct - propaga error cuando falla el repositorio")
    void saveProduct_error() {
        when(productMapper.toEntity(productRequest)).thenReturn(product);
        when(productRepository.save(product))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(productService.saveProduct(productRequest))
                .expectError(RuntimeException.class)
                .verify();
    }

    // ───────────────── updateProduct ─────────────────

    @Test
    @DisplayName("updateProduct - actualiza correctamente cuando el producto existe")
    void updateProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Mono.just(product));
        doNothing().when(productMapper).updateEntityFromRequest(productRequest, product);
        when(productRepository.save(product)).thenReturn(Mono.just(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);

        StepVerifier.create(productService.updateProduct(1L, productRequest))
                .expectNext(productResponse)
                .verifyComplete();

        verify(productMapper).updateEntityFromRequest(productRequest, product);
    }

    @Test
    @DisplayName("updateProduct - lanza NOT_FOUND cuando el producto no existe")
    void updateProduct_notFound() {
        when(productRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProduct(99L, productRequest))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    // ───────────────── getAll ─────────────────

    @Test
    @DisplayName("getAll - retorna todos los productos")
    void getAll_success() {
        Product product2 = new Product();
        product2.setId(2L);
        ProductResponse response2 = new ProductResponse();
        response2.setId(2L);

        when(productRepository.findAll()).thenReturn(Flux.just(product, product2));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(productMapper.toResponse(product2)).thenReturn(response2);

        StepVerifier.create(productService.getAll())
                .expectNext(productResponse)
                .expectNext(response2)
                .verifyComplete();
    }

    @Test
    @DisplayName("getAll - retorna vacío cuando no hay productos")
    void getAll_empty() {
        when(productRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(productService.getAll())
                .verifyComplete();
    }

    // ───────────────── deleteProduct ─────────────────

    @Test
    @DisplayName("deleteProduct - elimina correctamente cuando el producto existe")
    void deleteProduct_success() {
        when(productRepository.findById(1L)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(1L))
                .verifyComplete();

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct - lanza NOT_FOUND cuando el producto no existe")
    void deleteProduct_notFound() {
        when(productRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(99L))
                .expectErrorMatches(e -> e instanceof ResponseStatusException
                        && ((ResponseStatusException) e).getStatusCode() == HttpStatus.NOT_FOUND)
                .verify();
    }

    // ───────────────── getMostPopular ─────────────────

    @Test
    @DisplayName("getMostPopular - retorna producto más popular por sucursal")
    void getMostPopular_withProducts() {
        Branch branch = new Branch();
        branch.setId(1L);
        branch.setName("Sucursal Central");

        BranchProductResponse bpResponse = new BranchProductResponse();
        bpResponse.setBranchId(1L);

        when(branchRepository.findAll()).thenReturn(Flux.just(branch));
        when(productRepository.findMostPopularByBranchId(1L)).thenReturn(Mono.just(productInBranch));
        when(branchProductMapper.toResponse(eq(branch), anyList())).thenReturn(bpResponse);

        StepVerifier.create(productService.getMostPopular())
                .expectNext(bpResponse)
                .verifyComplete();
    }

    @Test
    @DisplayName("getMostPopular - retorna lista vacía para sucursales sin producto")
    void getMostPopular_noBranchProducts() {
        Branch branch = new Branch();
        branch.setId(1L);

        BranchProductResponse bpResponse = new BranchProductResponse();
        bpResponse.setBranchId(1L);

        when(branchRepository.findAll()).thenReturn(Flux.just(branch));
        when(productRepository.findMostPopularByBranchId(1L)).thenReturn(Mono.empty());
        when(branchProductMapper.toResponse(eq(branch), anyList())).thenReturn(bpResponse);

        StepVerifier.create(productService.getMostPopular())
                .expectNext(bpResponse)
                .verifyComplete();
    }
}
