package com.cloudwebframeworks.webfluxr2dbc.service;

import com.cloudwebframeworks.webfluxr2dbc.dto.ProductResponse;
import com.cloudwebframeworks.webfluxr2dbc.model.Product;
import com.cloudwebframeworks.webfluxr2dbc.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<ProductResponse> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse);
    }

    public Flux<ProductResponse> getProducts() {
        return productRepository.findTop100ByOrderByIdAsc()
                .map(this::toResponse);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }
}