package com.cloudwebframeworks.webfluxr2dbc.service;

import com.cloudwebframeworks.webfluxr2dbc.dto.OrderItemResponse;
import com.cloudwebframeworks.webfluxr2dbc.dto.OrderResponse;
import com.cloudwebframeworks.webfluxr2dbc.model.Order;
import com.cloudwebframeworks.webfluxr2dbc.model.OrderItem;
import com.cloudwebframeworks.webfluxr2dbc.model.Product;
import com.cloudwebframeworks.webfluxr2dbc.repository.OrderItemRepository;
import com.cloudwebframeworks.webfluxr2dbc.repository.OrderRepository;
import com.cloudwebframeworks.webfluxr2dbc.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import com.cloudwebframeworks.webfluxr2dbc.dto.CreateOrderRequest;
import com.cloudwebframeworks.webfluxr2dbc.dto.CreateOrderItemRequest;

import java.time.LocalDateTime;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    public Mono<OrderResponse> getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order ->
                        orderItemRepository.findByOrderId(order.getId())
                                .flatMap(this::toItemResponse)
                                .collectList()
                                .map(items -> toOrderResponse(order, items))
                );
    }

    public Flux<OrderResponse> getOrdersByCustomerId(Long customerId) {
    return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
            .flatMap(order ->
                    orderItemRepository.findByOrderId(order.getId())
                            .flatMap(this::toItemResponse)
                            .collectList()
                            .map(items -> toOrderResponse(order, items))
            );
    }

    public Mono<OrderResponse> createOrder(CreateOrderRequest request) {
    Order order = new Order(
            null,
            request.customerId(),
            "CREATED",
            LocalDateTime.now()
    );

    return orderRepository.save(order)
            .flatMap(savedOrder ->
                    Flux.fromIterable(request.items())
                            .map(item -> toOrderItem(savedOrder.getId(), item))
                            .flatMap(orderItemRepository::save)
                            .then(getOrderById(savedOrder.getId()))
            );
}

    private OrderItem toOrderItem(Long orderId, CreateOrderItemRequest item) {
        return new OrderItem(
                null,
                orderId,
                item.productId(),
                item.quantity()
        );
    }

    private Mono<OrderItemResponse> toItemResponse(OrderItem item) {
        return productRepository.findById(item.getProductId())
                .map(product -> toItemResponse(item, product));
    }

    private OrderItemResponse toItemResponse(OrderItem item, Product product) {
        return new OrderItemResponse(
                item.getProductId(),
                product.getName(),
                item.getQuantity(),
                product.getPrice()
        );
    }

    private OrderResponse toOrderResponse(Order order, List<OrderItemResponse> items) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                items
        );
    }
}