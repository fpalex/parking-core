package com.estapar.parking.persistence;

import com.estapar.parking.domain.model.Spot;
import java.util.Optional;

public interface SpotPersistence {
    Optional<Spot> findById(Long id);
    Optional<Spot> findByLatAndLng(Double lat, Double lng);
    Spot save(Spot spot);
}