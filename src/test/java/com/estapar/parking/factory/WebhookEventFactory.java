package com.estapar.parking.factory;

import com.estapar.parking.domain.model.WebhookEvent;
import java.time.LocalDateTime;

public class WebhookEventFactory {

    public static WebhookEvent buildEntry(String plate) {
        return WebhookEvent.builder()
                .licensePlate(plate)
                .entryTime(LocalDateTime.of(2026, 5, 13, 12, 0, 0))
                .eventType("ENTRY")
                .build();
    }

    public static WebhookEvent buildParked(String plate) {
        return WebhookEvent.builder()
                .licensePlate(plate)
                .lat(-23.561684)
                .lng(-46.655981)
                .eventType("PARKED")
                .build();
    }

    public static WebhookEvent buildExit(String plate, LocalDateTime exitTime) {
        return WebhookEvent.builder()
                .licensePlate(plate)
                .exitTime(exitTime)
                .eventType("EXIT")
                .build();
    }
}