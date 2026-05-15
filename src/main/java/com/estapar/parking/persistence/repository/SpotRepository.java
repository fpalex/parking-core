package com.estapar.parking.persistence.repository;

import com.estapar.parking.persistence.repository.entity.SpotEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface SpotRepository extends JpaRepository<SpotEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<SpotEntity> findByLatAndLng(Double lat, Double lng);
}