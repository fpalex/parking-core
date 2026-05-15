package com.estapar.parking.service.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.service.PricingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

@Slf4j
@Service
public class PricingServiceImpl implements PricingService {

    private static final long FREE_MINUTES_IN_SECONDS = 30 * 60;
    private static final double OCCUPANCY_LOW = 0.25;
    private static final double OCCUPANCY_MEDIUM = 0.50;
    private static final double OCCUPANCY_HIGH = 0.75;
    private static final BigDecimal MULTIPLIER_DISCOUNT = BigDecimal.valueOf(0.90);
    private static final BigDecimal MULTIPLIER_NORMAL = BigDecimal.ONE;
    private static final BigDecimal MULTIPLIER_HIGH = BigDecimal.valueOf(1.10);
    private static final BigDecimal MULTIPLIER_PEAK = BigDecimal.valueOf(1.25);

    @Override
    public BigDecimal calculateMultiplier(final Sector sector) {
        double occupancyRate = (double) sector.getCurrentOccupancy() / sector.getMaxCapacity();
        log.info("Calculating multiplier for sector {} with occupancy rate {}",
                sector.getName(), occupancyRate);

        if (occupancyRate < OCCUPANCY_LOW) return MULTIPLIER_DISCOUNT;
        if (occupancyRate < OCCUPANCY_MEDIUM) return MULTIPLIER_NORMAL;
        if (occupancyRate < OCCUPANCY_HIGH) return MULTIPLIER_HIGH;
        return MULTIPLIER_PEAK;
    }

    @Override
    public BigDecimal calculatePrice(final VehicleRecord record, final WebhookEvent event, final Sector sector) {
        long seconds = Duration.between(record.getEntryTime(), event.getExitTime()).toSeconds();

        if (seconds <= FREE_MINUTES_IN_SECONDS) {
            log.info("Vehicle {} stayed {} seconds — free of charge", event.getLicensePlate(), seconds);
            return BigDecimal.ZERO;
        }

        long hours = (long) Math.ceil(seconds / 3600.0);

        return sector.getBasePrice()
                .multiply(BigDecimal.valueOf(hours))
                .multiply(record.getPriceMultiplier())
                .setScale(2, RoundingMode.HALF_UP);
    }
}