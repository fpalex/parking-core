package com.estapar.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Spot {
    private Long id;
    private Sector sector;
    private Double lat;
    private Double lng;
    private Boolean occupied;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}