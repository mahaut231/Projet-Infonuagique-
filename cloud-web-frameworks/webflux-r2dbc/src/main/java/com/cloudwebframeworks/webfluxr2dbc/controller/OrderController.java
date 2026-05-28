package com.cloudwebframeworks.webfluxr2dbc.controller;

import com.cloudwebframeworks.webfluxr2dbc.dto.OrderResponse;
import com.cloudwebframeworks.webfluxr2dbc.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import com.cloudwebframeworks.webfluxr2dbc.dto.CreateOrderRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderResponse>> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request)
                .map(ResponseEntity::ok);
    }
}