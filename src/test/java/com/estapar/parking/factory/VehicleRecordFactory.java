package com.estapar.parking.factory;

import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.domain.model.VehicleRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VehicleRecordFactory {

    public static VehicleRecord build(Spot spot) {
        return build(spot, LocalDateTime.of(2026, 5, 13, 12, 0, 0), BigDecimal.ONE);
    }

    public static VehicleRecord build(Spot spot, LocalDateTime entryTime, BigDecimal multiplier) {
        return VehicleRecord.builder()
                .licensePlate("ABC1234")
                .entryTime(entryTime)
                .spot(spot)
                .priceMultiplier(multiplier)
                .build();
    }
}