package com.estapar.parking.controller.impl;

import com.estapar.parking.domain.model.RevenueFilter;
import com.estapar.parking.domain.model.RevenueResult;
import com.estapar.parking.mapper.RevenueMapperImpl;
import com.estapar.parking.service.RevenueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RevenueControllerImpl.class)
@Import({RevenueMapperImpl.class})
class RevenueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RevenueService revenueService;

    private RevenueResult buildRevenueResult() {
        return RevenueResult.builder()
                .amount(BigDecimal.valueOf(150.0))
                .build();
    }

    @Test
    @DisplayName("Should return 200 with revenue when valid request is made")
    void shouldReturn200WithRevenueWhenValidRequestIsMade() throws Exception {
        when(revenueService.getRevenue(any(RevenueFilter.class))).thenReturn(buildRevenueResult());

        mockMvc.perform(get("/revenue")
                        .param("sector", "A")
                        .param("date", "2026-05-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(150.0))
                .andExpect(jsonPath("$.currency").value("BRL"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(revenueService).getRevenue(any(RevenueFilter.class));
    }

    @Test
    @DisplayName("Should return 400 when sector is missing")
    void shouldReturn400WhenSectorIsMissing() throws Exception {
        mockMvc.perform(get("/revenue")
                        .param("date", "2026-05-15"))
                .andExpect(status().isBadRequest());

        verify(revenueService, never()).getRevenue(any());
    }

    @Test
    @DisplayName("Should return 400 when date is missing")
    void shouldReturn400WhenDateIsMissing() throws Exception {
        mockMvc.perform(get("/revenue")
                        .param("sector", "A"))
                .andExpect(status().isBadRequest());

        verify(revenueService, never()).getRevenue(any());
    }

    @Test
    @DisplayName("Should return 500 when service throws exception")
    void shouldReturn500WhenServiceThrowsException() throws Exception {
        when(revenueService.getRevenue(any(RevenueFilter.class)))
                .thenThrow(new RuntimeException("unexpected error"));

        mockMvc.perform(get("/revenue")
                        .param("sector", "A")
                        .param("date", "2026-05-15"))
                .andExpect(status().isInternalServerError());
    }
}