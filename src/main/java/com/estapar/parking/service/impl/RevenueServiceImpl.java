package com.estapar.parking.service.impl;

import com.estapar.parking.domain.model.RevenueFilter;
import com.estapar.parking.domain.model.RevenueResult;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.persistence.SectorPersistence;
import com.estapar.parking.persistence.VehicleRecordPersistence;
import com.estapar.parking.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {

    private final VehicleRecordPersistence vehicleRecordPersistence;
    private final SectorPersistence sectorPersistence;

    @Override
    @Transactional(readOnly = true)
    public RevenueResult getRevenue(final RevenueFilter filter) {
        sectorPersistence.findByName(filter.getSector())
                .orElseThrow(() -> new BusinessException("Sector not found: " + filter.getSector()));

        LocalDateTime start = filter.getDate().atStartOfDay();
        LocalDateTime end = filter.getDate().plusDays(1).atStartOfDay();

        BigDecimal revenue = vehicleRecordPersistence.sumRevenueBySectorAndDate(filter.getSector(), start, end);
        log.info("Revenue for sector {} on {}: {}", filter.getSector(), filter.getDate(), revenue);

        return RevenueResult.builder()
                .amount(revenue)
                .build();
    }
}