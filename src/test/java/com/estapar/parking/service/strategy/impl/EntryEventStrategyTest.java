package com.estapar.parking.service.strategy.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.factory.VehicleRecordFactory;
import com.estapar.parking.factory.WebhookEventFactory;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import com.estapar.parking.service.PricingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntryEventStrategyTest {

    @Mock
    private SectorPersistence sectorPersistence;

    @Mock
    private VehicleRecordPersistence vehicleRecordPersistence;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private EntryEventStrategy entryEventStrategy;

    @Test
    @DisplayName("Should return ENTRY as event type")
    void shouldReturnEntryAsEventType() {
        assertEquals("ENTRY", entryEventStrategy.getEventType());
    }

    @Test
    @DisplayName("Should handle entry successfully when sector has available capacity")
    void shouldHandleEntrySuccessfullyWhenSectorHasAvailableCapacity() {
        Sector sector = SectorFactory.build(0, 10);
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");

        when(sectorPersistence.findFirstWithAvailableCapacity()).thenReturn(Optional.of(sector));
        when(pricingService.calculateMultiplier(sector)).thenReturn(BigDecimal.valueOf(0.90));
        when(sectorPersistence.save(any())).thenReturn(sector);
        when(vehicleRecordPersistence.save(any())).thenReturn(VehicleRecord.builder().build());

        entryEventStrategy.handle(event);

        verify(sectorPersistence).save(any(Sector.class));
        verify(vehicleRecordPersistence).save(any(VehicleRecord.class));
        assertEquals(1, sector.getCurrentOccupancy());
    }

    @Test
    @DisplayName("Should throw exception when garage is full")
    void shouldThrowExceptionWhenGarageIsFull() {
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");
        when(sectorPersistence.findFirstWithAvailableCapacity()).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> entryEventStrategy.handle(event));
        verify(vehicleRecordPersistence, never()).save(any());
    }

    @Test
    @DisplayName("Should apply 10% discount when occupancy is below 25%")
    void shouldApply10PercentDiscountWhenOccupancyIsBelow25Percent() {
        Sector sector = SectorFactory.build(2, 10);
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");

        when(sectorPersistence.findFirstWithAvailableCapacity()).thenReturn(Optional.of(sector));
        when(pricingService.calculateMultiplier(sector)).thenReturn(BigDecimal.valueOf(0.90));
        when(sectorPersistence.save(any())).thenReturn(sector);

        VehicleRecord savedRecord = VehicleRecord.builder().build();
        when(vehicleRecordPersistence.save(any())).thenAnswer(inv -> {
            VehicleRecord r = inv.getArgument(0);
            savedRecord.setPriceMultiplier(r.getPriceMultiplier());
            return r;
        });

        entryEventStrategy.handle(event);

        assertEquals(BigDecimal.valueOf(0.90), savedRecord.getPriceMultiplier());
    }

    @Test
    @DisplayName("Should apply no discount when occupancy is between 25% and 50%")
    void shouldApplyNoDiscountWhenOccupancyIsBetween25And50Percent() {
        Sector sector = SectorFactory.build(4, 10);
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");

        when(sectorPersistence.findFirstWithAvailableCapacity()).thenReturn(Optional.of(sector));
        when(pricingService.calculateMultiplier(sector)).thenReturn(BigDecimal.ONE);
        when(sectorPersistence.save(any())).thenReturn(sector);

        VehicleRecord savedRecord = VehicleRecord.builder().build();
        when(vehicleRecordPersistence.save(any())).thenAnswer(inv -> {
            VehicleRecord r = inv.getArgument(0);
            savedRecord.setPriceMultiplier(r.getPriceMultiplier());
            return r;
        });

        entryEventStrategy.handle(event);

        assertEquals(BigDecimal.ONE, savedRecord.getPriceMultiplier());
    }

    @Test
    @DisplayName("Should apply 10% increase when occupancy is between 50% and 75%")
    void shouldApply10PercentIncreaseWhenOccupancyIsBetween50And75Percent() {
        Sector sector = SectorFactory.build(6, 10);
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");

        when(sectorPersistence.findFirstWithAvailableCapacity()).thenReturn(Optional.of(sector));
        when(pricingService.calculateMultiplier(sector)).thenReturn(BigDecimal.valueOf(1.10));
        when(sectorPersistence.save(any())).thenReturn(sector);

        VehicleRecord savedRecord = VehicleRecord.builder().build();
        when(vehicleRecordPersistence.save(any())).thenAnswer(inv -> {
            VehicleRecord r = inv.getArgument(0);
            savedRecord.setPriceMultiplier(r.getPriceMultiplier());
            return r;
        });

        entryEventStrategy.handle(event);

        assertEquals(BigDecimal.valueOf(1.10), savedRecord.getPriceMultiplier());
    }

    @Test
    @DisplayName("Should apply 25% increase when occupancy is above 75%")
    void shouldApply25PercentIncreaseWhenOccupancyIsAbove75Percent() {
        Sector sector = SectorFactory.build(8, 10);
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");

        when(sectorPersistence.findFirstWithAvailableCapacity()).thenReturn(Optional.of(sector));
        when(pricingService.calculateMultiplier(sector)).thenReturn(BigDecimal.valueOf(1.25));
        when(sectorPersistence.save(any())).thenReturn(sector);

        VehicleRecord savedRecord = VehicleRecord.builder().build();
        when(vehicleRecordPersistence.save(any())).thenAnswer(inv -> {
            VehicleRecord r = inv.getArgument(0);
            savedRecord.setPriceMultiplier(r.getPriceMultiplier());
            return r;
        });

        entryEventStrategy.handle(event);

        assertEquals(BigDecimal.valueOf(1.25), savedRecord.getPriceMultiplier());
    }

    @Test
    @DisplayName("Should throw exception when vehicle is already inside the garage")
    void shouldThrowExceptionWhenVehicleIsAlreadyInsideTheGarage() {
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");
        VehicleRecord existingRecord = VehicleRecordFactory.build(null);

        when(vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234"))
                .thenReturn(Optional.of(existingRecord));

        assertThrows(BusinessException.class, () -> entryEventStrategy.handle(event));
        verify(sectorPersistence, never()).findFirstWithAvailableCapacity();
        verify(vehicleRecordPersistence, never()).save(any());
    }
}