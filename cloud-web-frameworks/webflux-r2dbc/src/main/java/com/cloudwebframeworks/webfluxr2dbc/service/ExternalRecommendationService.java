package com.cloudwebframeworks.webfluxr2dbc.service;

import com.cloudwebframeworks.webfluxr2dbc.dto.RecommendationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalRecommendationService {

    private final WebClient recommendationWebClient;

    public ExternalRecommendationService(WebClient recommendationWebClient) {
        this.recommendationWebClient = recommendationWebClient;
    }

    public Mono<RecommendationResponse> getRecommendations(Long productId, int delayMs) {
        return recommendationWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recommendations/{productId}")
                        .queryParam("delayMs", delayMs)
                        .build(productId))
                .retrieve()
                .bodyToMono(RecommendationResponse.class);
    }
}