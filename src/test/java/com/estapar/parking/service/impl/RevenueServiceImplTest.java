package com.estapar.parking.service.impl;

import com.estapar.parking.domain.model.RevenueFilter;
import com.estapar.parking.domain.model.RevenueResult;
import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RevenueServiceImplTest {

    @Mock
    private VehicleRecordPersistence vehicleRecordPersistence;

    @Mock
    private SectorPersistence sectorPersistence;

    @InjectMocks
    private RevenueServiceImpl revenueService;

    @Test
    @DisplayName("Should return revenue when records exist for sector and date")
    void shouldReturnRevenueWhenRecordsExistForSectorAndDate() {
        LocalDate date = LocalDate.of(2026, 5, 15);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        Sector sector = SectorFactory.build();

        RevenueFilter filter = RevenueFilter.builder()
                .sector("A")
                .date(date)
                .build();

        when(sectorPersistence.findByName("A")).thenReturn(Optional.of(sector));
        when(vehicleRecordPersistence.sumRevenueBySectorAndDate("A", start, end))
                .thenReturn(BigDecimal.valueOf(150.0));

        RevenueResult result = revenueService.getRevenue(filter);

        assertNotNull(result);
        assertEquals(0, result.getAmount().compareTo(BigDecimal.valueOf(150.0)));
        verify(sectorPersistence).findByName("A");
        verify(vehicleRecordPersistence).sumRevenueBySectorAndDate("A", start, end);
    }

    @Test
    @DisplayName("Should return zero when no records exist for sector and date")
    void shouldReturnZeroWhenNoRecordsExistForSectorAndDate() {
        LocalDate date = LocalDate.of(2026, 5, 15);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        Sector sector = SectorFactory.build();

        RevenueFilter filter = RevenueFilter.builder()
                .sector("B")
                .date(date)
                .build();

        when(sectorPersistence.findByName("B")).thenReturn(Optional.of(sector));
        when(vehicleRecordPersistence.sumRevenueBySectorAndDate("B", start, end))
                .thenReturn(BigDecimal.ZERO);

        RevenueResult result = revenueService.getRevenue(filter);

        assertNotNull(result);
        assertEquals(0, result.getAmount().compareTo(BigDecimal.ZERO));
        verify(sectorPersistence).findByName("B");
        verify(vehicleRecordPersistence).sumRevenueBySectorAndDate("B", start, end);
    }

    @Test
    @DisplayName("Should throw BusinessException when sector not found")
    void shouldThrowBusinessExceptionWhenSectorNotFound() {
        RevenueFilter filter = RevenueFilter.builder()
                .sector("Z")
                .date(LocalDate.of(2026, 5, 15))
                .build();

        when(sectorPersistence.findByName("Z")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> revenueService.getRevenue(filter));
        verify(vehicleRecordPersistence, never()).sumRevenueBySectorAndDate(any(), any(), any());
    }
}