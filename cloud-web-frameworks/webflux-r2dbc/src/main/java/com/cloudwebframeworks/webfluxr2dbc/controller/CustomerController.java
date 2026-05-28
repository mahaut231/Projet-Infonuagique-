package com.cloudwebframeworks.webfluxr2dbc.controller;

import com.cloudwebframeworks.webfluxr2dbc.dto.OrderResponse;
import com.cloudwebframeworks.webfluxr2dbc.service.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final OrderService orderService;

    public CustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{customerId}/orders")
    public Flux<OrderResponse> getOrdersByCustomerId(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }
}