package com.cloudwebframeworks.webfluxr2dbc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient recommendationWebClient(
            @Value("${mock.service.base-url}") String mockServiceBaseUrl
    ) {
        return WebClient.builder()
                .baseUrl(mockServiceBaseUrl)
                .build();
    }
}