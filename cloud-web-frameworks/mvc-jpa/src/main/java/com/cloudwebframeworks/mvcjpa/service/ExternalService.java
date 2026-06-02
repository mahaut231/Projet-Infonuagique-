package com.cloudwebframeworks.mvcjpa.service;

import com.cloudwebframeworks.mvcjpa.dto.Dtos.RecommendationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExternalService {

    private final RestClient restClient;

    public ExternalService(@Value("${mock.service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Appel bloquant vers le mock-service.
     * Illustre le comportement impératif : le thread est suspendu pendant toute la durée de la latence.
     */
    public RecommendationResponse getRecommendations(Long productId, int delayMs) {
        try {
            return restClient.get()
                    .uri("/recommendations/{id}?delayMs={delay}", productId, delayMs)
                    .retrieve()
                    .body(RecommendationResponse.class);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Mock service unavailable: " + e.getMessage());
        }
    }
}
