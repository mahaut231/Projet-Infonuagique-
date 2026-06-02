package com.cloudwebframeworks.mvcjpa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// ── Produit ────────────────────────────────────────────────────────────────

public class Dtos {

    public record ProductResponse(Long id, String name, BigDecimal price, Integer stock) {}

    // ── Commande ───────────────────────────────────────────────────────────────

    public record OrderItemResponse(Long id, ProductResponse product, Integer quantity) {}

    public record OrderResponse(
            Long id,
            Long customerId,
            String status,
            LocalDateTime createdAt,
            List<OrderItemResponse> items
    ) {}

    // ── Création de commande ───────────────────────────────────────────────────

    public record CreateOrderItemRequest(
            @NotNull Long productId,
            @NotNull @Min(1) Integer quantity
    ) {}

    public record CreateOrderRequest(
            @NotNull Long customerId,
            @NotNull @Size(min = 1) List<CreateOrderItemRequest> items
    ) {}

    // ── Appel externe ──────────────────────────────────────────────────────────

    public record RecommendationResponse(Long productId, List<Long> recommendedProductIds, Integer delayMs) {}
}
