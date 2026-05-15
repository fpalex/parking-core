package com.estapar.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class RevenueResult {
    private BigDecimal amount;
}
