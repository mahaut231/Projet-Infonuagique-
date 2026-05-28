package com.cloudwebframeworks.webfluxr2dbc.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}