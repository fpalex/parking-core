package com.estapar.parking.config;

import com.estapar.parking.domain.model.GarageConfig;
import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.factory.SpotFactory;
import com.estapar.parking.integration.GarageSimulatorIntegration;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.SpotPersistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageInitializerTest {

    @Mock
    private GarageSimulatorIntegration garageSimulatorIntegration;

    @Mock
    private SectorPersistence sectorPersistence;

    @Mock
    private SpotPersistence spotPersistence;

    @Mock
    private ApplicationArguments applicationArguments;

    @InjectMocks
    private GarageInitializer garageInitializer;

    @Test
    @DisplayName("Should save sector and spot when they do not exist")
    void shouldSaveSectorAndSpotWhenTheyDoNotExist() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);

        GarageConfig config = GarageConfig.builder()
                .sectors(List.of(sector))
                .spots(List.of(spot))
                .build();

        when(garageSimulatorIntegration.fetchGarageConfig()).thenReturn(config);
        when(sectorPersistence.findByName("A"))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(sector));
        when(sectorPersistence.save(any())).thenReturn(sector);
        when(spotPersistence.findById(1L)).thenReturn(Optional.empty());
        when(spotPersistence.save(any())).thenReturn(spot);

        garageInitializer.run(applicationArguments);

        verify(sectorPersistence).save(any(Sector.class));
        verify(spotPersistence).save(any(Spot.class));
    }

    @Test
    @DisplayName("Should skip sector when it already exists")
    void shouldSkipSectorWhenItAlreadyExists() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);

        GarageConfig config = GarageConfig.builder()
                .sectors(List.of(sector))
                .spots(List.of(spot))
                .build();

        when(garageSimulatorIntegration.fetchGarageConfig()).thenReturn(config);
        when(sectorPersistence.findByName("A")).thenReturn(Optional.of(sector));
        when(spotPersistence.findById(1L)).thenReturn(Optional.empty());
        when(sectorPersistence.findByName(spot.getSector().getName())).thenReturn(Optional.of(sector));
        when(spotPersistence.save(any())).thenReturn(spot);

        garageInitializer.run(applicationArguments);

        verify(sectorPersistence, never()).save(any(Sector.class));
        verify(spotPersistence).save(any(Spot.class));
    }

    @Test
    @DisplayName("Should skip spot when it already exists")
    void shouldSkipSpotWhenItAlreadyExists() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);

        GarageConfig config = GarageConfig.builder()
                .sectors(List.of(sector))
                .spots(List.of(spot))
                .build();

        when(garageSimulatorIntegration.fetchGarageConfig()).thenReturn(config);
        when(sectorPersistence.findByName("A")).thenReturn(Optional.empty());
        when(sectorPersistence.save(any())).thenReturn(sector);
        when(spotPersistence.findById(1L)).thenReturn(Optional.of(spot));

        garageInitializer.run(applicationArguments);

        verify(sectorPersistence).save(any(Sector.class));
        verify(spotPersistence, never()).save(any(Spot.class));
    }

    @Test
    @DisplayName("Should throw BusinessException when sector not found for spot")
    void shouldThrowBusinessExceptionWhenSectorNotFoundForSpot() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);

        GarageConfig config = GarageConfig.builder()
                .sectors(List.of(sector))
                .spots(List.of(spot))
                .build();

        when(garageSimulatorIntegration.fetchGarageConfig()).thenReturn(config);
        when(sectorPersistence.findByName("A")).thenReturn(Optional.empty());
        when(sectorPersistence.save(any())).thenReturn(sector);
        when(spotPersistence.findById(1L)).thenReturn(Optional.empty());
        when(sectorPersistence.findByName(spot.getSector().getName())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> garageInitializer.run(applicationArguments));
        verify(spotPersistence, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when integration fails")
    void shouldThrowExceptionWhenIntegrationFails() {
        when(garageSimulatorIntegration.fetchGarageConfig())
                .thenThrow(new RuntimeException("connection refused"));

        assertThrows(RuntimeException.class, () -> garageInitializer.run(applicationArguments));
        verify(sectorPersistence, never()).save(any());
        verify(spotPersistence, never()).save(any());
    }
}