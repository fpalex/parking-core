package com.estapar.parking.persistence.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.mapper.SectorMapper;
import com.estapar.parking.exception.PersistenceException;
import com.estapar.parking.persistence.repository.SectorRepository;
import com.estapar.parking.persistence.repository.entity.SectorEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SectorPersistenceImplTest {

    @Mock
    private SectorRepository sectorRepository;

    @Mock
    private SectorMapper sectorMapper;

    @InjectMocks
    private SectorPersistenceImpl sectorPersistence;

    @Test
    @DisplayName("Should return sector when available capacity exists")
    void shouldReturnSectorWhenAvailableCapacityExists() {
        Sector sector = SectorFactory.build(0, 10);
        SectorEntity entity = new SectorEntity();

        when(sectorRepository.findFirstWithAvailableCapacity()).thenReturn(Optional.of(entity));
        when(sectorMapper.toDomain(entity)).thenReturn(sector);

        Optional<Sector> result = sectorPersistence.findFirstWithAvailableCapacity();

        assertTrue(result.isPresent());
        assertEquals(sector, result.get());
        verify(sectorRepository).findFirstWithAvailableCapacity();
    }

    @Test
    @DisplayName("Should return empty when no available capacity exists")
    void shouldReturnEmptyWhenNoAvailableCapacityExists() {
        when(sectorRepository.findFirstWithAvailableCapacity()).thenReturn(Optional.empty());

        Optional<Sector> result = sectorPersistence.findFirstWithAvailableCapacity();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw PersistenceException when findFirstWithAvailableCapacity fails")
    void shouldThrowPersistenceExceptionWhenFindFirstWithAvailableCapacityFails() {
        when(sectorRepository.findFirstWithAvailableCapacity()).thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> sectorPersistence.findFirstWithAvailableCapacity());

        assertTrue(exception.getMessage().contains("Error finding sector with available capacity"));
        assertTrue(exception.getMessage().contains("db error"));
    }

    @Test
    @DisplayName("Should return sector when found by name")
    void shouldReturnSectorWhenFoundByName() {
        Sector sector = SectorFactory.build();
        SectorEntity entity = new SectorEntity();

        when(sectorRepository.findByName("A")).thenReturn(Optional.of(entity));
        when(sectorMapper.toDomain(entity)).thenReturn(sector);

        Optional<Sector> result = sectorPersistence.findByName("A");

        assertTrue(result.isPresent());
        assertEquals(sector, result.get());
        verify(sectorRepository).findByName("A");
    }

    @Test
    @DisplayName("Should throw PersistenceException when findByName fails")
    void shouldThrowPersistenceExceptionWhenFindByNameFails() {
        when(sectorRepository.findByName("A")).thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> sectorPersistence.findByName("A"));

        assertTrue(exception.getMessage().contains("Error finding sector by name"));
        assertTrue(exception.getMessage().contains("db error"));
    }

    @Test
    @DisplayName("Should save sector successfully")
    void shouldSaveSectorSuccessfully() {
        Sector sector = SectorFactory.build();
        SectorEntity entity = new SectorEntity();

        when(sectorMapper.toEntity(sector)).thenReturn(entity);
        when(sectorRepository.save(entity)).thenReturn(entity);
        when(sectorMapper.toDomain(entity)).thenReturn(sector);

        Sector result = sectorPersistence.save(sector);

        assertNotNull(result);
        assertEquals(sector, result);
        verify(sectorRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw PersistenceException when save fails")
    void shouldThrowPersistenceExceptionWhenSaveFails() {
        Sector sector = SectorFactory.build();
        SectorEntity entity = new SectorEntity();

        when(sectorMapper.toEntity(sector)).thenReturn(entity);
        when(sectorRepository.save(entity)).thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> sectorPersistence.save(sector));

        assertTrue(exception.getMessage().contains("Error saving sector"));
        assertTrue(exception.getMessage().contains("db error"));
    }
}