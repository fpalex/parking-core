package com.estapar.parking.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RevenueResponseDto {
    private BigDecimal amount;
    private String currency = "BRL";
    private LocalDateTime timestamp;
}