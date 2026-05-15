package com.estapar.parking.persistence.impl;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.mapper.SectorMapper;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.exception.PersistenceException;
import com.estapar.parking.persistence.repository.SectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SectorPersistenceImpl implements SectorPersistence {

    private final SectorRepository sectorRepository;
    private final SectorMapper sectorMapper;

    @Override
    public Optional<Sector> findFirstWithAvailableCapacity() {
        try {
            return sectorRepository.findFirstWithAvailableCapacity()
                    .map(sectorMapper::toDomain);
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error finding sector with available capacity. %s", e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    @Override
    public Optional<Sector> findByName(final String name) {
        try {
            return sectorRepository.findByName(name)
                    .map(sectorMapper::toDomain);
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error finding sector by name: %s. %s", name, e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    @Override
    public Sector save(final Sector sector) {
        try {
            return sectorMapper.toDomain(
                    sectorRepository.save(sectorMapper.toEntity(sector)));
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error saving sector: %s. %s", sector.getName(), e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }
}