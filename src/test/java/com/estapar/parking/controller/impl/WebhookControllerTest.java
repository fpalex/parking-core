package com.estapar.parking.controller.impl;

import com.estapar.parking.controller.WebhookController;
import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.dto.WebhookEventDto;
import com.estapar.parking.mapper.RevenueMapperImpl;
import com.estapar.parking.mapper.WebhookMapperImpl;
import com.estapar.parking.service.WebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
@Import({WebhookMapperImpl.class, RevenueMapperImpl.class})
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WebhookService webhookService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private WebhookEventDto buildEntryEvent() {
        WebhookEventDto event = new WebhookEventDto();
        event.setLicensePlate("ABC1234");
        event.setEntryTime(LocalDateTime.of(2026, 5, 13, 12, 0, 0));
        event.setEventType("ENTRY");
        return event;
    }

    private WebhookEventDto buildParkedEvent() {
        WebhookEventDto event = new WebhookEventDto();
        event.setLicensePlate("ABC1234");
        event.setLat(-23.561684);
        event.setLng(-46.655981);
        event.setEventType("PARKED");
        return event;
    }

    private WebhookEventDto buildExitEvent() {
        WebhookEventDto event = new WebhookEventDto();
        event.setLicensePlate("ABC1234");
        event.setExitTime(LocalDateTime.of(2026, 5, 13, 14, 30, 0));
        event.setEventType("EXIT");
        return event;
    }

    @Test
    @DisplayName("Should return 200 when entry event is received")
    void shouldReturn200WhenEntryEventIsReceived() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildEntryEvent())))
                .andExpect(status().isOk());

        verify(webhookService).handle(any(WebhookEvent.class));
    }

    @Test
    @DisplayName("Should return 200 when parked event is received")
    void shouldReturn200WhenParkedEventIsReceived() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildParkedEvent())))
                .andExpect(status().isOk());

        verify(webhookService).handle(any(WebhookEvent.class));
    }

    @Test
    @DisplayName("Should return 200 when exit event is received")
    void shouldReturn200WhenExitEventIsReceived() throws Exception {
        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildExitEvent())))
                .andExpect(status().isOk());

        verify(webhookService).handle(any(WebhookEvent.class));
    }

    @Test
    @DisplayName("Should return 400 when license plate is missing")
    void shouldReturn400WhenLicensePlateIsMissing() throws Exception {
        WebhookEventDto event = new WebhookEventDto();
        event.setEventType("ENTRY");

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 500 when service throws exception")
    void shouldReturn500WhenServiceThrowsException() throws Exception {
        doThrow(new RuntimeException("unexpected error")).when(webhookService).handle(any());

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildEntryEvent())))
                .andExpect(status().isInternalServerError());
    }
}