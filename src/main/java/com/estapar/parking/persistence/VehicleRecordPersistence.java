package com.estapar.parking.persistence;

import com.estapar.parking.domain.model.VehicleRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VehicleRecordPersistence {
    Optional<VehicleRecord> findByLicensePlateAndExitTimeIsNull(String licensePlate);
    BigDecimal sumRevenueBySectorAndDate(String sector, LocalDateTime start, LocalDateTime end);
    VehicleRecord save(VehicleRecord record);
}