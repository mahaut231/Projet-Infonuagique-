package com.cloudwebframeworks.mvcvirtualthreads.service;

import com.cloudwebframeworks.mvcvirtualthreads.dto.Dtos.*;
import com.cloudwebframeworks.mvcvirtualthreads.model.*;
import com.cloudwebframeworks.mvcvirtualthreads.repository.CustomerRepository;
import com.cloudwebframeworks.mvcvirtualthreads.repository.OrderRepository;
import com.cloudwebframeworks.mvcvirtualthreads.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id) {
        Order order = orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Order not found: " + id));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByCustomerId(Long customerId) {
        // Vérifie que le client existe
        if (!customerRepository.existsById(customerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Customer not found: " + customerId);
        }
        return orderRepository.findByCustomerIdWithItems(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found: " + request.customerId()));

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = new ArrayList<>();
        for (CreateOrderItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found: " + itemReq.productId()));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.quantity());
            items.add(item);
        }

        order.setItems(items);
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    // ── Mapping ────────────────────────────────────────────────────────────────

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> itemResponses = o.getItems() == null ? List.of() :
                o.getItems().stream().map(this::toItemResponse).toList();

        return new OrderResponse(
                o.getId(),
                o.getCustomer().getId(),
                o.getStatus(),
                o.getCreatedAt(),
                itemResponses
        );
    }

    private OrderItemResponse toItemResponse(OrderItem i) {
        Product p = i.getProduct();
        ProductResponse productResponse = new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStock());
        return new OrderItemResponse(i.getId(), productResponse, i.getQuantity());
    }
}
