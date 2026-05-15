package com.estapar.parking.service.strategy;

import com.estapar.parking.domain.model.WebhookEvent;

public interface WebhookEventStrategy {
    void handle(WebhookEvent event);
    String getEventType();
}