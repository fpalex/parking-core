package com.estapar.parking.persistence.repository;

import com.estapar.parking.persistence.repository.entity.VehicleRecordEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface VehicleRecordRepository extends JpaRepository<VehicleRecordEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<VehicleRecordEntity> findByLicensePlateAndExitTimeIsNull(String licensePlate);

    @Query("""
       SELECT COALESCE(SUM(v.priceCharged), 0)
         FROM VehicleRecordEntity v
        WHERE v.spot.sector.name = :sector
          AND v.exitTime >= :start
          AND v.exitTime < :end
    """)
    BigDecimal sumRevenueBySectorAndDate(
            @Param("sector") String sector,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}