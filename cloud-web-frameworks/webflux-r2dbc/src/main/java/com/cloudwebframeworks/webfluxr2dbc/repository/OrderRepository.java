package com.cloudwebframeworks.webfluxr2dbc.repository;

import com.cloudwebframeworks.webfluxr2dbc.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    Flux<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}