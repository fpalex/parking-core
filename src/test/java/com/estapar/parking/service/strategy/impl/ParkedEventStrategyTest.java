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
import com.estapar.parking.persistence.SpotPersistence;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkedEventStrategyTest {

    @Mock
    private SpotPersistence spotPersistence;

    @Mock
    private VehicleRecordPersistence vehicleRecordPersistence;

    @InjectMocks
    private ParkedEventStrategy parkedEventStrategy;

    @Test
    @DisplayName("Should return PARKED as event type")
    void shouldReturnParkedAsEventType() {
        assertEquals("PARKED", parkedEventStrategy.getEventType());
    }

    @Test
    @DisplayName("Should handle parked successfully when spot and record are found")
    void shouldHandleParkedSuccessfullyWhenSpotAndRecordAreFound() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector);
        VehicleRecord record = VehicleRecordFactory.build(null);
        WebhookEvent event = WebhookEventFactory.buildParked("ABC1234");

        when(spotPersistence.findByLatAndLng(-23.561684, -46.655981)).thenReturn(Optional.of(spot));
        when(vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.of(record));
        when(spotPersistence.save(any())).thenReturn(spot);
        when(vehicleRecordPersistence.save(any())).thenReturn(record);

        parkedEventStrategy.handle(event);

        verify(spotPersistence).save(any(Spot.class));
        verify(vehicleRecordPersistence).save(any(VehicleRecord.class));
        assertTrue(spot.getOccupied());
        assertEquals(spot, record.getSpot());
    }

    @Test
    @DisplayName("Should throw exception when spot not found")
    void shouldThrowExceptionWhenSpotNotFound() {
        WebhookEvent event = WebhookEventFactory.buildParked("ABC1234");
        when(spotPersistence.findByLatAndLng(any(), any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parkedEventStrategy.handle(event));
        verify(vehicleRecordPersistence, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when active record not found")
    void shouldThrowExceptionWhenActiveRecordNotFound() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector);
        WebhookEvent event = WebhookEventFactory.buildParked("ABC1234");

        when(spotPersistence.findByLatAndLng(any(), any())).thenReturn(Optional.of(spot));
        when(vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> parkedEventStrategy.handle(event));
        verify(spotPersistence, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when spot is already occupied")
    void shouldThrowExceptionWhenSpotIsAlreadyOccupied() {
        Sector sector = SectorFactory.build(1, 10);
        Spot spot = SpotFactory.build(sector, true);
        WebhookEvent event = WebhookEventFactory.buildParked("ABC1234");

        when(spotPersistence.findByLatAndLng(-23.561684, -46.655981)).thenReturn(Optional.of(spot));

        assertThrows(BusinessException.class, () -> parkedEventStrategy.handle(event));
        verify(vehicleRecordPersistence, never()).save(any());
    }
}