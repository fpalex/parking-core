package com.estapar.parking.service.strategy.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.SpotPersistence;
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
public class ExitEventStrategy implements WebhookEventStrategy {

    private final SectorPersistence sectorPersistence;
    private final SpotPersistence spotPersistence;
    private final VehicleRecordPersistence vehicleRecordPersistence;
    private final PricingService pricingService;

    @Override
    public String getEventType() {
        return "EXIT";
    }

    @Override
    @Transactional
    public void handle(final WebhookEvent event) {
        log.info("Handling EXIT for plate: {}", event.getLicensePlate());

        VehicleRecord record = vehicleRecordPersistence
                .findByLicensePlateAndExitTimeIsNull(event.getLicensePlate())
                .orElseThrow(() -> new BusinessException("Active record not found for plate: "
                        + event.getLicensePlate()));

        Spot spot = record.getSpot();
        Sector sector = spot.getSector();

        BigDecimal price = pricingService.calculatePrice(record, event, sector);
        record.setPriceCharged(price);
        record.setExitTime(event.getExitTime());
        vehicleRecordPersistence.save(record);

        spot.setOccupied(false);
        spotPersistence.save(spot);

        sector.setCurrentOccupancy(Math.max(0, sector.getCurrentOccupancy() - 1));
        sectorPersistence.save(sector);

        log.info("Vehicle {} exited. Charged: {} BRL", event.getLicensePlate(), price);
    }
}