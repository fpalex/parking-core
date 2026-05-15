package com.estapar.parking.controller;

import com.estapar.parking.dto.WebhookEventDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Webhook", description = "Receives events from the garage simulator")
@RequestMapping("/webhook")
public interface WebhookController {

    @PostMapping
    @Operation(summary = "Receive event", description = "Processes ENTRY, PARKED and EXIT events from the simulator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    ResponseEntity<Void> handleEvent(@Valid @RequestBody WebhookEventDto event);
}