package com.cloudwebframeworks.mvcjpa.controller;

import com.cloudwebframeworks.mvcjpa.dto.Dtos.RecommendationResponse;
import com.cloudwebframeworks.mvcjpa.service.ExternalService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/external")
public class ExternalController {

    private final ExternalService externalService;

    public ExternalController(ExternalService externalService) {
        this.externalService = externalService;
    }

    /**
     * Appel externe simulé vers le mock-service avec latence configurable.
     * Modèle bloquant : le thread Tomcat attend la réponse HTTP pendant toute la durée delayMs.
     * C'est exactement ce que SEDA cherchait à éviter, et ce que WebFlux/Virtual Threads résolvent différemment.
     */
    @GetMapping("/recommendations/{id}")
    public RecommendationResponse getRecommendations(
            @PathVariable Long id,
            @RequestParam(defaultValue = "300") int delayMs) {
        return externalService.getRecommendations(id, delayMs);
    }
}
