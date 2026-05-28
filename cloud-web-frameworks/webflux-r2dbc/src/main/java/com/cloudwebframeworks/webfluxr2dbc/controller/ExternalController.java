package com.cloudwebframeworks.webfluxr2dbc.controller;

import com.cloudwebframeworks.webfluxr2dbc.dto.RecommendationResponse;
import com.cloudwebframeworks.webfluxr2dbc.service.ExternalRecommendationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/external")
public class ExternalController {

    private final ExternalRecommendationService externalRecommendationService;

    public ExternalController(ExternalRecommendationService externalRecommendationService) {
        this.externalRecommendationService = externalRecommendationService;
    }

    @GetMapping("/recommendations/{productId}")
    public Mono<RecommendationResponse> getRecommendations(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "300") int delayMs
    ) {
        return externalRecommendationService.getRecommendations(productId, delayMs);
    }
}