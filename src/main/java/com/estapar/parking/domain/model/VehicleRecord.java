package com.estapar.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class VehicleRecord {
    private Long id;
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Spot spot;
    private BigDecimal priceMultiplier;
    private BigDecimal priceCharged;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}