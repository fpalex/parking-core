package com.estapar.parking.service.strategy.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import com.estapar.parking.service.PricingService;
import com.estapar.parking.service.strategy.WebhookEventStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class EntryEventStrategy implements WebhookEventStrategy {

    private final SectorPersistence sectorPersistence;
    private final VehicleRecordPersistence vehicleRecordPersistence;
    private final PricingService pricingService;

    @Override
    public String getEventType() {
        return "ENTRY";
    }

    @Override
    @Transactional
    public void handle(final WebhookEvent event) {
        log.info("Handling ENTRY for plate: {}", event.getLicensePlate());

        vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull(event.getLicensePlate())
                .ifPresent(record -> {
                    throw new BusinessException("Vehicle already inside the garage: "
                            + event.getLicensePlate());
                });

        Sector sector = sectorPersistence.findFirstWithAvailableCapacity()
                .orElseThrow(() -> new BusinessException("Garage is full, no available spots"));

        BigDecimal multiplier = pricingService.calculateMultiplier(sector);

        sector.setCurrentOccupancy(sector.getCurrentOccupancy() + 1);
        sectorPersistence.save(sector);

        VehicleRecord record = VehicleRecord.builder()
                .licensePlate(event.getLicensePlate())
                .entryTime(event.getEntryTime())
                .priceMultiplier(multiplier)
                .build();
        vehicleRecordPersistence.save(record);

        log.info("Vehicle {} entered sector {} with multiplier {}",
                event.getLicensePlate(), sector.getName(), multiplier);
    }
}