package com.estapar.parking.persistence;

import com.estapar.parking.domain.model.Sector;

import java.util.Optional;

public interface SectorPersistence {
    Optional<Sector> findFirstWithAvailableCapacity();
    Optional<Sector> findByName(String name);
    Sector save(Sector sector);
}