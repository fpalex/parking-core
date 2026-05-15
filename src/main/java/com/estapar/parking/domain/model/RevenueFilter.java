package com.estapar.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RevenueFilter {
    private String sector;
    private LocalDate date;
}
