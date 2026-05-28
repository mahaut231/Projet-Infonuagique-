package com.cloudwebframeworks.webfluxr2dbc.dto;

import java.util.List;

public record RecommendationResponse(
        Long productId,
        List<Long> recommendedProductIds,
        Integer delayMs
) {
}