package com.estapar.parking.service.strategy.impl;

import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.persistence.SpotPersistence;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import com.estapar.parking.service.strategy.WebhookEventStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkedEventStrategy implements WebhookEventStrategy {

    private final SpotPersistence spotPersistence;
    private final VehicleRecordPersistence vehicleRecordPersistence;

    @Override
    public String getEventType() {
        return "PARKED";
    }

    @Override
    @Transactional
    public void handle(final WebhookEvent event) {
        log.info("Handling PARKED for plate: {}", event.getLicensePlate());

        Spot spot = spotPersistence.findByLatAndLng(event.getLat(), event.getLng())
                .orElseThrow(() -> new BusinessException("Spot not found for coordinates: "
                        + event.getLat() + ", " + event.getLng()));

        if (spot.getOccupied()) {
            throw new BusinessException("Spot is already occupied at coordinates: "
                    + event.getLat() + ", " + event.getLng());
        }

        VehicleRecord record = vehicleRecordPersistence
                .findByLicensePlateAndExitTimeIsNull(event.getLicensePlate())
                .orElseThrow(() -> new BusinessException("Active record not found for plate: "
                        + event.getLicensePlate()));

        spot.setOccupied(true);
        record.setSpot(spot);
        spotPersistence.save(spot);
        vehicleRecordPersistence.save(record);

        log.info("Vehicle {} confirmed at spot {}", event.getLicensePlate(), spot.getId());
    }
}