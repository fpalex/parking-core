package com.estapar.parking.integration.client;

import com.estapar.parking.config.FeignConfig;
import com.estapar.parking.integration.client.dto.SimulatorGarageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "GarageSimulatorClient", url = "${simulator.url}", configuration = FeignConfig.class)
public interface GarageSimulatorClient {

    @GetMapping(value = "/garage", consumes = "text/plain")
    @Retry(name = "api")
    SimulatorGarageResponse fetchGarageConfig();
}