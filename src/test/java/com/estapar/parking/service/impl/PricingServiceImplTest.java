package com.estapar.parking.service.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.factory.SpotFactory;
import com.estapar.parking.factory.VehicleRecordFactory;
import com.estapar.parking.factory.WebhookEventFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PricingServiceImplTest {

    @InjectMocks
    private PricingServiceImpl pricingService;

    @Test
    @DisplayName("Should apply 10% discount when occupancy is below 25%")
    void shouldApply10PercentDiscountWhenOccupancyIsBelow25Percent() {
        Sector sector = SectorFactory.build(2, 10);

        BigDecimal multiplier = pricingService.calculateMultiplier(sector);

        assertEquals(BigDecimal.valueOf(0.90), multiplier);
    }

    @Test
    @DisplayName("Should apply no discount when occupancy is between 25% and 50%")
    void shouldApplyNoDiscountWhenOccupancyIsBetween25And50Percent() {
        Sector sector = SectorFactory.build(4, 10);

        BigDecimal multiplier = pricingService.calculateMultiplier(sector);

        assertEquals(BigDecimal.ONE, multiplier);
    }

    @Test
    @DisplayName("Should apply 10% increase when occupancy is between 50% and 75%")
    void shouldApply10PercentIncreaseWhenOccupancyIsBetween50And75Percent() {
        Sector sector = SectorFactory.build(6, 10);

        BigDecimal multiplier = pricingService.calculateMultiplier(sector);

        assertEquals(BigDecimal.valueOf(1.10), multiplier);
    }

    @Test
    @DisplayName("Should apply 25% increase when occupancy is above 75%")
    void shouldApply25PercentIncreaseWhenOccupancyIsAbove75Percent() {
        Sector sector = SectorFactory.build(8, 10);

        BigDecimal multiplier = pricingService.calculateMultiplier(sector);

        assertEquals(BigDecimal.valueOf(1.25), multiplier);
    }

    @Test
    @DisplayName("Should return zero when vehicle stayed exactly 30 minutes")
    void shouldReturnZeroWhenVehicleStayedExactly30Minutes() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector, true);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 12, 30, 0));

        BigDecimal price = pricingService.calculatePrice(record, event, sector);

        assertEquals(BigDecimal.ZERO, price);
    }

    @Test
    @DisplayName("Should charge when vehicle stayed 30 minutes and some seconds")
    void shouldChargeWhenVehicleStayed30MinutesAndSomeSeconds() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector, true);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 12, 30, 45));

        BigDecimal price = pricingService.calculatePrice(record, event, sector);

        assertTrue(price.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should charge correct price for 2 hours and 30 minutes")
    void shouldChargeCorrectPriceFor2HoursAnd30Minutes() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector, true);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 14, 30, 0));

        BigDecimal price = pricingService.calculatePrice(record, event, sector);

        assertEquals(0, price.compareTo(BigDecimal.valueOf(121.50)));
    }
}