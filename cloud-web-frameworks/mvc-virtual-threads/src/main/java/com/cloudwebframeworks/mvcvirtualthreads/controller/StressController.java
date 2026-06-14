package com.cloudwebframeworks.mvcvirtualthreads.controller;

import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@RestController
@RequestMapping("/stress")
public class StressController {

    /**
     * Calcule le n-ième nombre de Fibonacci de façon naïve pour saturer le CPU.
     * Identique à ce que fait la version WebFlux pour rendre les benchmarks comparables.
     */
    @GetMapping("/cpu")
    public Map<String, Object> cpuStress(@RequestParam(defaultValue = "35") int n) {
        long result = fibonacci(n);
        return Map.of("n", n, "result", result);
    }

    private long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }
}
