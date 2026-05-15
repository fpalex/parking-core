package com.estapar.parking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(description = "Webhook event received from the garage simulator")
public class WebhookEventDto {

    @NotBlank(message = "License plate is required")
    @JsonProperty("license_plate")
    @Schema(description = "Vehicle license plate", example = "ABC1234")
    private String licensePlate;

    @JsonProperty("entry_time")
    @Schema(description = "Vehicle entry time (required for ENTRY events)", example = "2026-05-15T12:00:00.000Z")
    private LocalDateTime entryTime;

    @JsonProperty("exit_time")
    @Schema(description = "Vehicle exit time (required for EXIT events)", example = "2026-05-15T14:00:00.000Z")
    private LocalDateTime exitTime;

    @Schema(description = "Spot latitude (required for PARKED events)", example = "-23.561684")
    private Double lat;

    @Schema(description = "Spot longitude (required for PARKED events)", example = "-46.655981")
    private Double lng;

    @NotBlank(message = "Event type is required")
    @JsonProperty("event_type")
    @Schema(description = "Event type", example = "ENTRY", allowableValues = {"ENTRY", "PARKED", "EXIT"})
    private String eventType;
}