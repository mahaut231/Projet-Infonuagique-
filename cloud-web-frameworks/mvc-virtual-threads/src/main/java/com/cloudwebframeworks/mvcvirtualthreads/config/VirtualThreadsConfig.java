package com.cloudwebframeworks.mvcvirtualthreads.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class VirtualThreadsConfig {

    /**
     * Force Tomcat à utiliser des virtual threads pour chaque requête HTTP.
     * Avec spring.threads.virtual.enabled=true dans application.yml,
     * Spring Boot 3.2+ configure automatiquement Tomcat avec cet executor.
     * Ce bean le rend explicite et visible pour le benchmark.
     */
    @Bean
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
