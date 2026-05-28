package com.cloudwebframeworks.webfluxr2dbc.dto;

public record CpuStressResponse(
        int n,
        long result,
        long durationMs
) {
}