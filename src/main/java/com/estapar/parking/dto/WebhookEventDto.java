package com.estapar.parking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WebhookEventDto {

    @JsonProperty("license_plate")
    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @JsonProperty("entry_time")
    private LocalDateTime entryTime;

    @JsonProperty("exit_time")
    private LocalDateTime exitTime;

    private Double lat;
    private Double lng;

    @JsonProperty("event_type")
    @NotBlank(message = "Event type is required")
    private String eventType;
}