package com.cloudwebframeworks.webfluxr2dbc.service;

import com.cloudwebframeworks.webfluxr2dbc.dto.CpuStressResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class CpuStressService {

    public Mono<CpuStressResponse> compute(int n) {
        return Mono.fromCallable(() -> {
            long start = System.currentTimeMillis();
            long result = fibonacci(n);
            long duration = System.currentTimeMillis() - start;
            return new CpuStressResponse(n, result, duration);
        }).subscribeOn(Schedulers.parallel());
    }

    private long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}