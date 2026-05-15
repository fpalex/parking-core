package com.estapar.parking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "Revenue query parameters")
public class RevenueRequestDto {

    @NotBlank(message = "Sector is required")
    @Schema(description = "Garage sector name", example = "A")
    private String sector;

    @NotNull(message = "Date is required")
    @Schema(description = "Date to query revenue", example = "2026-05-15")
    private LocalDate date;
}