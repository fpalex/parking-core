package com.estapar.parking.persistence.impl;

import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.mapper.VehicleRecordMapper;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import com.estapar.parking.exception.PersistenceException;
import com.estapar.parking.persistence.repository.VehicleRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleRecordPersistenceImpl implements VehicleRecordPersistence {

    private final VehicleRecordRepository vehicleRecordRepository;
    private final VehicleRecordMapper vehicleRecordMapper;

    @Override
    public Optional<VehicleRecord> findByLicensePlateAndExitTimeIsNull(final String licensePlate) {
        try {
            return vehicleRecordRepository.findByLicensePlateAndExitTimeIsNull(licensePlate)
                    .map(vehicleRecordMapper::toDomain);
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error finding active vehicle record for plate: %s. %s", licensePlate, e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumRevenueBySectorAndDate(final String sector, final LocalDateTime start, final LocalDateTime end) {
        try {
            return vehicleRecordRepository.sumRevenueBySectorAndDate(sector, start, end);
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error calculating revenue for sector: %s. %s", sector, e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }

    @Override
    public VehicleRecord save(final VehicleRecord record) {
        try {
            return vehicleRecordMapper.toDomain(
                    vehicleRecordRepository.save(vehicleRecordMapper.toEntity(record)));
        } catch (Exception e) {
            final var errorMsg = String.format(
                    "Error saving vehicle record for plate: %s. %s", record.getLicensePlate(), e.getMessage());
            log.error(errorMsg);
            throw new PersistenceException(errorMsg);
        }
    }
}