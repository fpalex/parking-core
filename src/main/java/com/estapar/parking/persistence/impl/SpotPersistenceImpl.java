package com.estapar.parking.persistence.impl;

import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.mapper.SpotMapper;
import com.estapar.parking.persistence.SpotPersistence;
import com.estapar.parking.exception.PersistenceException;
import com.estapar.parking.persistence.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpotPersistenceImpl implements SpotPersistence {

    private final SpotRepository spotRepository;
    private final SpotMapper spotMapper;

    @Override
    public Optional<Spot> findById(final Long id) {
        try {
            return spotRepository.findById(id)
                    .map(spotMapper::toDomain);
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error finding spot by id: %s. %s", id, e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    @Override
    public Optional<Spot> findByLatAndLng(final Double lat, final Double lng) {
        try {
            return spotRepository.findByLatAndLng(lat, lng)
                    .map(spotMapper::toDomain);
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error finding spot by coordinates: %s, %s. %s", lat, lng, e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    @Override
    public Spot save(final Spot spot) {
        try {
            return spotMapper.toDomain(
                    spotRepository.save(spotMapper.toEntity(spot)));
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error saving spot: %s. %s", spot.getId(), e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }
}