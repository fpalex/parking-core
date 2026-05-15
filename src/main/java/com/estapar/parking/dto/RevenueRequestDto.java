package com.estapar.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RevenueRequestDto {

    @NotBlank(message = "Sector is required")
    private String sector;

    @NotNull(message = "Date is required")
    private LocalDate date;
}