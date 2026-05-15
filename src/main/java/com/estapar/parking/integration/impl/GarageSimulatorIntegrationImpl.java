package com.estapar.parking.integration.impl;

import com.estapar.parking.domain.model.GarageConfig;
import com.estapar.parking.exception.IntegrationException;
import com.estapar.parking.integration.GarageSimulatorIntegration;
import com.estapar.parking.integration.client.GarageSimulatorClient;
import com.estapar.parking.mapper.GarageConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GarageSimulatorIntegrationImpl implements GarageSimulatorIntegration {

    private final GarageSimulatorClient garageSimulatorClient;
    private final GarageConfigMapper garageConfigMapper;

    @Override
    public GarageConfig fetchGarageConfig() {
        try {
            return garageConfigMapper.toDomain(garageSimulatorClient.fetchGarageConfig());
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error fetching garage configuration from simulator. %s", e.getMessage());
            log.error(errorMsg);
            throw new IntegrationException(errorMsg);
        }
    }
}