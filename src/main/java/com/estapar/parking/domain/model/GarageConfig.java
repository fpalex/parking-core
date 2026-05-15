package com.estapar.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GarageConfig {
    private List<Sector> sectors;
    private List<Spot> spots;
}