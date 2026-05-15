package com.estapar.parking.controller.impl;

import com.estapar.parking.controller.RevenueController;
import com.estapar.parking.dto.RevenueRequestDto;
import com.estapar.parking.dto.RevenueResponseDto;
import com.estapar.parking.mapper.RevenueMapper;
import com.estapar.parking.service.RevenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RevenueControllerImpl implements RevenueController {

    private final RevenueService revenueService;
    private final RevenueMapper revenueMapper;

    @Override
    public ResponseEntity<RevenueResponseDto> getRevenue(final RevenueRequestDto request) {
        log.info("Revenue request for sector: {} on date: {}", request.getSector(), request.getDate());
        return ResponseEntity.ok(revenueMapper.toDto(revenueService.getRevenue(revenueMapper.toDomain(request))));
    }
}
