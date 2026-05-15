package com.estapar.parking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Revenue query response")
public class RevenueResponseDto {

    @Schema(description = "Total revenue amount", example = "121.50")
    private BigDecimal amount;

    @Schema(description = "Currency code", example = "BRL")
    private String currency;

    @Schema(description = "Response timestamp", example = "2026-05-15T14:30:00.000Z")
    private LocalDateTime timestamp;
}