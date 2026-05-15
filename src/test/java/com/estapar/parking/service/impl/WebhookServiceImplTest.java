package com.estapar.parking.service.impl;

import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.factory.WebhookEventFactory;
import com.estapar.parking.service.strategy.WebhookEventStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class WebhookServiceImplTest {

    @Mock
    private WebhookEventStrategy entryStrategy;

    @Mock
    private WebhookEventStrategy parkedStrategy;

    @Mock
    private WebhookEventStrategy exitStrategy;

    private WebhookServiceImpl webhookService;

    @BeforeEach
    void setUp() {
        when(entryStrategy.getEventType()).thenReturn("ENTRY");
        when(parkedStrategy.getEventType()).thenReturn("PARKED");
        when(exitStrategy.getEventType()).thenReturn("EXIT");
        webhookService = new WebhookServiceImpl(List.of(entryStrategy, parkedStrategy, exitStrategy));
    }

    @Test
    @DisplayName("Should delegate to entry strategy when event type is ENTRY")
    void shouldDelegateToEntryStrategyWhenEventTypeIsEntry() {
        WebhookEvent event = WebhookEventFactory.buildEntry("ABC1234");

        webhookService.handle(event);

        verify(entryStrategy).handle(event);
        verify(parkedStrategy, never()).handle(any());
        verify(exitStrategy, never()).handle(any());
    }

    @Test
    @DisplayName("Should delegate to parked strategy when event type is PARKED")
    void shouldDelegateToParkedStrategyWhenEventTypeIsParked() {
        WebhookEvent event = WebhookEventFactory.buildParked("ABC1234");

        webhookService.handle(event);

        verify(parkedStrategy).handle(event);
        verify(entryStrategy, never()).handle(any());
        verify(exitStrategy, never()).handle(any());
    }

    @Test
    @DisplayName("Should delegate to exit strategy when event type is EXIT")
    void shouldDelegateToExitStrategyWhenEventTypeIsExit() {
        WebhookEvent event = WebhookEventFactory.buildExit("ABC1234",
                LocalDateTime.of(2026, 5, 13, 14, 0, 0));

        webhookService.handle(event);

        verify(exitStrategy).handle(event);
        verify(entryStrategy, never()).handle(any());
        verify(parkedStrategy, never()).handle(any());
    }

    @Test
    @DisplayName("Should throw exception when event type is unknown")
    void shouldThrowExceptionWhenEventTypeIsUnknown() {
        WebhookEvent event = WebhookEvent.builder()
                .eventType("UNKNOWN")
                .build();

        assertThrows(BusinessException.class, () -> webhookService.handle(event));
        verify(entryStrategy, never()).handle(any());
        verify(parkedStrategy, never()).handle(any());
        verify(exitStrategy, never()).handle(any());
    }
}