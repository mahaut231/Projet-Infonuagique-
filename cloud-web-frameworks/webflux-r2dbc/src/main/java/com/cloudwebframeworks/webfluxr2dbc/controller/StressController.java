package com.cloudwebframeworks.webfluxr2dbc.controller;

import com.cloudwebframeworks.webfluxr2dbc.dto.CpuStressResponse;
import com.cloudwebframeworks.webfluxr2dbc.service.CpuStressService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stress")
public class StressController {

    private final CpuStressService cpuStressService;

    public StressController(CpuStressService cpuStressService) {
        this.cpuStressService = cpuStressService;
    }

    @GetMapping("/cpu")
    public Mono<CpuStressResponse> cpuStress(@RequestParam(defaultValue = "35") int n) {
        return cpuStressService.compute(n);
    }
}