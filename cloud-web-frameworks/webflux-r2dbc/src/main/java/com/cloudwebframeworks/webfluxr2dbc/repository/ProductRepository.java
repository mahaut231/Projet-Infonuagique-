package com.cloudwebframeworks.webfluxr2dbc.repository;

import com.cloudwebframeworks.webfluxr2dbc.model.Product;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    Flux<Product> findTop100ByOrderByIdAsc();
}