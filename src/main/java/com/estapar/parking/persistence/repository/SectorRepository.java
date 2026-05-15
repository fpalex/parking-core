package com.estapar.parking.persistence.repository;

import com.estapar.parking.persistence.repository.entity.SectorEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SectorRepository extends JpaRepository<SectorEntity, Long> {
    Optional<SectorEntity> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SectorEntity s WHERE s.currentOccupancy < s.maxCapacity ORDER BY s.currentOccupancy DESC LIMIT 1")
    Optional<SectorEntity> findFirstWithAvailableCapacity();
}