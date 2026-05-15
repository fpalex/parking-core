package com.estapar.parking.integration.impl;

import com.estapar.parking.domain.model.GarageConfig;
import com.estapar.parking.exception.IntegrationException;
import com.estapar.parking.integration.client.GarageSimulatorClient;
import com.estapar.parking.integration.client.dto.SimulatorGarageResponse;
import com.estapar.parking.mapper.GarageConfigMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageSimulatorIntegrationImplTest {

    @Mock
    private GarageSimulatorClient garageSimulatorClient;

    @Mock
    private GarageConfigMapper garageConfigMapper;

    @InjectMocks
    private GarageSimulatorIntegrationImpl garageSimulatorIntegration;

    @Test
    @DisplayName("Should return garage config successfully when client is available")
    void shouldReturnGarageConfigSuccessfullyWhenClientIsAvailable() {
        SimulatorGarageResponse response = new SimulatorGarageResponse();
        response.setGarage(List.of());
        response.setSpots(List.of());

        GarageConfig config = GarageConfig.builder()
                .sectors(List.of())
                .spots(List.of())
                .build();

        when(garageSimulatorClient.fetchGarageConfig()).thenReturn(response);
        when(garageConfigMapper.toDomain(response)).thenReturn(config);

        GarageConfig result = garageSimulatorIntegration.fetchGarageConfig();

        assertNotNull(result);
        verify(garageSimulatorClient).fetchGarageConfig();
        verify(garageConfigMapper).toDomain(response);
    }

    @Test
    @DisplayName("Should throw IntegrationException when client fails")
    void shouldThrowIntegrationExceptionWhenClientFails() {
        when(garageSimulatorClient.fetchGarageConfig()).thenThrow(new RuntimeException("connection refused"));

        IntegrationException exception = assertThrows(IntegrationException.class,
                () -> garageSimulatorIntegration.fetchGarageConfig());

        assertTrue(exception.getMessage().contains("Error fetching garage configuration from simulator"));
        assertTrue(exception.getMessage().contains("connection refused"));
        verify(garageSimulatorClient).fetchGarageConfig();
    }
}