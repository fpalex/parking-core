package com.estapar.parking.controller;

import com.estapar.parking.dto.RevenueRequestDto;
import com.estapar.parking.dto.RevenueResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Revenue", description = "Revenue queries by sector and date")
@RequestMapping("/revenue")
public interface RevenueController {

    @GetMapping
    @Operation(summary = "Get revenue", description = "Returns total revenue for a sector on a given date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Revenue returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<RevenueResponseDto> getRevenue(@ParameterObject @Valid RevenueRequestDto request);
}