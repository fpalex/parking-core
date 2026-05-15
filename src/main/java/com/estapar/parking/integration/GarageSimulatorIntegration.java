package com.estapar.parking.integration;

import com.estapar.parking.domain.model.GarageConfig;

public interface GarageSimulatorIntegration {
    GarageConfig fetchGarageConfig();
}