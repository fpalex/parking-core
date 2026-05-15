package com.estapar.parking.factory;

import com.estapar.parking.domain.model.Sector;
import java.math.BigDecimal;

public class SectorFactory {

    public static Sector build() {
        return build(0, 10);
    }

    public static Sector build(int occupancy, int maxCapacity) {
        return Sector.builder()
                .id(1L)
                .name("A")
                .basePrice(BigDecimal.valueOf(40.5))
                .maxCapacity(maxCapacity)
                .currentOccupancy(occupancy)
                .build();
    }
}