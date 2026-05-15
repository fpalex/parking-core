package com.estapar.parking.service.strategy.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.factory.SpotFactory;
import com.estapar.parking.factory.VehicleRecordFactory;
import com.estapar.parking.factory.WebhookEventFactory;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.SpotPersistence;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import com.estapar.parking.service.PricingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExitEventStrategyTest {

    @Mock
    private SectorPersistence sectorPersistence;

    @Mock
    private SpotPersistence spotPersistence;

    @Mock
    private VehicleRecordPersistence vehicleRecordPersistence;

    @Mock
    private PricingService pricingService;

    @InjectMocks
    private ExitEventStrategy exitEventStrategy;

    @Test
    @DisplayName("Should return EXIT as event type")
    void shouldReturnExitAsEventType() {
        assertEquals("EXIT", exitEventStrategy.getEventType());
    }

    @Test
    @DisplayName("Should handle exit successfully and charge correct price")
    void shouldHandleExitSuccessfullyAndChargeCorrectPrice() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector, true);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 14, 30, 0));

        when(vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.of(record));
        when(pricingService.calculatePrice(record, event, sector)).thenReturn(BigDecimal.valueOf(121.50));
        when(vehicleRecordPersistence.save(any())).thenReturn(record);
        when(spotPersistence.save(any())).thenReturn(spot);
        when(sectorPersistence.save(any())).thenReturn(sector);

        exitEventStrategy.handle(event);

        verify(vehicleRecordPersistence).save(any(VehicleRecord.class));
        verify(spotPersistence).save(any(Spot.class));
        verify(sectorPersistence).save(any(Sector.class));
        assertFalse(spot.getOccupied());
        assertEquals(BigDecimal.valueOf(121.50), record.getPriceCharged());
    }

    @Test
    @DisplayName("Should not charge when vehicle stayed exactly 30 minutes")
    void shouldNotChargeWhenVehicleStayedExactly30Minutes() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector, true);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 12, 30, 0));

        when(vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.of(record));
        when(pricingService.calculatePrice(record, event, sector)).thenReturn(BigDecimal.ZERO);
        when(vehicleRecordPersistence.save(any())).thenReturn(record);
        when(spotPersistence.save(any())).thenReturn(spot);
        when(sectorPersistence.save(any())).thenReturn(sector);

        exitEventStrategy.handle(event);

        assertEquals(BigDecimal.ZERO, record.getPriceCharged());
    }

    @Test
    @DisplayName("Should charge when vehicle stayed 30 minutes and some seconds")
    void shouldChargeWhenVehicleStayed30MinutesAndSomeSeconds() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector, true);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 12, 30, 45));

        when(vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.of(record));
        when(pricingService.calculatePrice(record, event, sector)).thenReturn(BigDecimal.valueOf(40.50));
        when(vehicleRecordPersistence.save(any())).thenReturn(record);
        when(spotPersistence.save(any())).thenReturn(spot);
        when(sectorPersistence.save(any())).thenReturn(sector);

        exitEventStrategy.handle(event);

        assertTrue(record.getPriceCharged().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should throw exception when active record not found on exit")
    void shouldThrowExceptionWhenActiveRecordNotFoundOnExit() {
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 14, 0, 0));
        when(vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> exitEventStrategy.handle(event));
        verify(spotPersistence, never()).save(any());
    }
}