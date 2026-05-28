package com.cloudwebframeworks.webfluxr2dbc.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        String status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
}