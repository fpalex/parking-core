package com.estapar.parking.persistence.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.factory.SpotFactory;
import com.estapar.parking.factory.VehicleRecordFactory;
import com.estapar.parking.mapper.VehicleRecordMapper;
import com.estapar.parking.exception.PersistenceException;
import com.estapar.parking.persistence.repository.VehicleRecordRepository;
import com.estapar.parking.persistence.repository.entity.VehicleRecordEntity;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleRecordPersistenceImplTest {

    @Mock
    private VehicleRecordRepository vehicleRecordRepository;

    @Mock
    private VehicleRecordMapper vehicleRecordMapper;

    @InjectMocks
    private VehicleRecordPersistenceImpl vehicleRecordPersistence;

    @Test
    @DisplayName("Should return vehicle record when active record found by license plate")
    void shouldReturnVehicleRecordWhenActiveRecordFoundByLicensePlate() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        VehicleRecordEntity entity = new VehicleRecordEntity();

        when(vehicleRecordRepository.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.of(entity));
        when(vehicleRecordMapper.toDomain(entity)).thenReturn(record);

        Optional<VehicleRecord> result = vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234");

        assertTrue(result.isPresent());
        assertEquals(record, result.get());
        verify(vehicleRecordRepository).findByLicensePlateAndExitTimeIsNull("ABC1234");
    }

    @Test
    @DisplayName("Should return empty when no active record found by license plate")
    void shouldReturnEmptyWhenNoActiveRecordFoundByLicensePlate() {
        when(vehicleRecordRepository.findByLicensePlateAndExitTimeIsNull("ABC1234")).thenReturn(Optional.empty());

        Optional<VehicleRecord> result = vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw PersistenceException when findByLicensePlateAndExitTimeIsNull fails")
    void shouldThrowPersistenceExceptionWhenFindByLicensePlateAndExitTimeIsNullFails() {
        when(vehicleRecordRepository.findByLicensePlateAndExitTimeIsNull("ABC1234"))
                .thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> vehicleRecordPersistence.findByLicensePlateAndExitTimeIsNull("ABC1234"));

        assertTrue(exception.getMessage().contains("Error finding active vehicle record for plate"));
        assertTrue(exception.getMessage().contains("db error"));
    }

    @Test
    @DisplayName("Should return revenue when records exist for sector and date")
    void shouldReturnRevenueWhenRecordsExistForSectorAndDate() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 13, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 13, 23, 59, 59);

        when(vehicleRecordRepository.sumRevenueBySectorAndDate("A", start, end))
                .thenReturn(BigDecimal.valueOf(150.0));

        BigDecimal result = vehicleRecordPersistence.sumRevenueBySectorAndDate("A", start, end);

        assertEquals(0, result.compareTo(BigDecimal.valueOf(150.0)));
        verify(vehicleRecordRepository).sumRevenueBySectorAndDate("A", start, end);
    }

    @Test
    @DisplayName("Should throw PersistenceException when sumRevenueBySectorAndDate fails")
    void shouldThrowPersistenceExceptionWhenSumRevenueBySectorAndDateFails() {
        LocalDateTime start = LocalDateTime.of(2026, 5, 13, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 5, 13, 23, 59, 59);

        when(vehicleRecordRepository.sumRevenueBySectorAndDate("A", start, end))
                .thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> vehicleRecordPersistence.sumRevenueBySectorAndDate("A", start, end));

        assertTrue(exception.getMessage().contains("Error calculating revenue for sector"));
        assertTrue(exception.getMessage().contains("db error"));
    }

    @Test
    @DisplayName("Should save vehicle record successfully")
    void shouldSaveVehicleRecordSuccessfully() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        VehicleRecordEntity entity = new VehicleRecordEntity();

        when(vehicleRecordMapper.toEntity(record)).thenReturn(entity);
        when(vehicleRecordRepository.save(entity)).thenReturn(entity);
        when(vehicleRecordMapper.toDomain(entity)).thenReturn(record);

        VehicleRecord result = vehicleRecordPersistence.save(record);

        assertNotNull(result);
        assertEquals(record, result);
        verify(vehicleRecordRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw PersistenceException when save fails")
    void shouldThrowPersistenceExceptionWhenSaveFails() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);
        VehicleRecord record = VehicleRecordFactory.build(spot);
        VehicleRecordEntity entity = new VehicleRecordEntity();

        when(vehicleRecordMapper.toEntity(record)).thenReturn(entity);
        when(vehicleRecordRepository.save(entity)).thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> vehicleRecordPersistence.save(record));

        assertTrue(exception.getMessage().contains("Error saving vehicle record for plate"));
        assertTrue(exception.getMessage().contains("db error"));
    }
}