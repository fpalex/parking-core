package com.estapar.parking.persistence.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.factory.SectorFactory;
import com.estapar.parking.factory.SpotFactory;
import com.estapar.parking.mapper.SpotMapper;
import com.estapar.parking.exception.PersistenceException;
import com.estapar.parking.persistence.repository.SpotRepository;
import com.estapar.parking.persistence.repository.entity.SpotEntity;
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
class SpotPersistenceImplTest {

    @Mock
    private SpotRepository spotRepository;

    @Mock
    private SpotMapper spotMapper;

    @InjectMocks
    private SpotPersistenceImpl spotPersistence;

    @Test
    @DisplayName("Should return spot when found by lat and lng")
    void shouldReturnSpotWhenFoundByLatAndLng() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);
        SpotEntity entity = new SpotEntity();

        when(spotRepository.findByLatAndLng(-23.561684, -46.655981)).thenReturn(Optional.of(entity));
        when(spotMapper.toDomain(entity)).thenReturn(spot);

        Optional<Spot> result = spotPersistence.findByLatAndLng(-23.561684, -46.655981);

        assertTrue(result.isPresent());
        assertEquals(spot, result.get());
        verify(spotRepository).findByLatAndLng(-23.561684, -46.655981);
    }

    @Test
    @DisplayName("Should return empty when spot not found by lat and lng")
    void shouldReturnEmptyWhenSpotNotFoundByLatAndLng() {
        when(spotRepository.findByLatAndLng(any(), any())).thenReturn(Optional.empty());

        Optional<Spot> result = spotPersistence.findByLatAndLng(-23.561684, -46.655981);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw PersistenceException when findByLatAndLng fails")
    void shouldThrowPersistenceExceptionWhenFindByLatAndLngFails() {
        when(spotRepository.findByLatAndLng(any(), any())).thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> spotPersistence.findByLatAndLng(-23.561684, -46.655981));

        assertTrue(exception.getMessage().contains("Error finding spot by coordinates"));
        assertTrue(exception.getMessage().contains("db error"));
    }

    @Test
    @DisplayName("Should save spot successfully")
    void shouldSaveSpotSuccessfully() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);
        SpotEntity entity = new SpotEntity();

        when(spotMapper.toEntity(spot)).thenReturn(entity);
        when(spotRepository.save(entity)).thenReturn(entity);
        when(spotMapper.toDomain(entity)).thenReturn(spot);

        Spot result = spotPersistence.save(spot);

        assertNotNull(result);
        assertEquals(spot, result);
        verify(spotRepository).save(entity);
    }

    @Test
    @DisplayName("Should throw PersistenceException when save fails")
    void shouldThrowPersistenceExceptionWhenSaveFails() {
        Sector sector = SectorFactory.build();
        Spot spot = SpotFactory.build(sector);
        SpotEntity entity = new SpotEntity();

        when(spotMapper.toEntity(spot)).thenReturn(entity);
        when(spotRepository.save(entity)).thenThrow(new RuntimeException("db error"));

        PersistenceException exception = assertThrows(PersistenceException.class,
                () -> spotPersistence.save(spot));

        assertTrue(exception.getMessage().contains("Error saving spot"));
        assertTrue(exception.getMessage().contains("db error"));
    }
}