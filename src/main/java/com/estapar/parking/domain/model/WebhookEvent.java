package com.estapar.parking.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class WebhookEvent {
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Double lat;
    private Double lng;
    private String eventType;
}