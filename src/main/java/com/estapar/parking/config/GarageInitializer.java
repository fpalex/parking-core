package com.estapar.parking.config;

import com.estapar.parking.domain.model.GarageConfig;
import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.integration.GarageSimulatorIntegration;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.SpotPersistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GarageInitializer implements ApplicationRunner {

    private final GarageSimulatorIntegration garageSimulatorIntegration;
    private final SectorPersistence sectorPersistence;
    private final SpotPersistence spotPersistence;

    @Override
    public void run(final ApplicationArguments args) {
        log.info("Fetching garage configuration from simulator...");

        GarageConfig config = garageSimulatorIntegration.fetchGarageConfig();

        config.getSectors().forEach(this::saveSectorIfNotExists);
        config.getSpots().forEach(this::saveSpotIfNotExists);

        log.info("Garage configuration loaded successfully.");
    }

    private void saveSectorIfNotExists(final Sector sector) {
        if (sectorPersistence.findByName(sector.getName()).isPresent()) {
            return;
        }
        sectorPersistence.save(sector);
    }

    private void saveSpotIfNotExists(final Spot spot) {
        if (spotPersistence.findById(spot.getId()).isPresent()) {
            return;
        }

        Sector sector = sectorPersistence.findByName(spot.getSector().getName())
                .orElseThrow(() -> new BusinessException("Sector not found: " + spot.getSector().getName()));

        spotPersistence.save(Spot.builder()
                .id(spot.getId())
                .sector(sector)
                .lat(spot.getLat())
                .lng(spot.getLng())
                .occupied(false)
                .build());
    }
}