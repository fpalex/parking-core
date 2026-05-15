package com.estapar.parking.service;

import com.estapar.parking.domain.model.WebhookEvent;

public interface WebhookService {
    void handle(WebhookEvent event);
}