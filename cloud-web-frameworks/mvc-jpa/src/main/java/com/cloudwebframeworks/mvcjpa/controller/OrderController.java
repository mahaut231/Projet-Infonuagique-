package com.cloudwebframeworks.mvcjpa.controller;

import com.cloudwebframeworks.mvcjpa.dto.Dtos.CreateOrderRequest;
import com.cloudwebframeworks.mvcjpa.dto.Dtos.OrderResponse;
import com.cloudwebframeworks.mvcjpa.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        return orderService.findById(id);
    }

    @GetMapping("/customers/{customerId}/orders")
    public List<OrderResponse> getByCustomer(@PathVariable Long customerId) {
        return orderService.findByCustomerId(customerId);
    }

    @PostMapping("/orders")
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(request);
    }
}