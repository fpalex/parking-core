package com.estapar.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Sector {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Integer maxCapacity;
    private Integer currentOccupancy;
    private String openHour;
    private String closeHour;
    private Integer durationLimitMinutes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}