package com.estapar.parking.controller.impl;

import com.estapar.parking.controller.WebhookController;
import com.estapar.parking.dto.WebhookEventDto;
import com.estapar.parking.mapper.WebhookMapper;
import com.estapar.parking.service.WebhookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WebhookControllerImpl implements WebhookController {

    private final WebhookService webhookService;
    private final WebhookMapper webhookMapper;

    @Override
    public ResponseEntity<Void> handleEvent(WebhookEventDto event) {
        webhookService.handle(webhookMapper.toDomain(event));
        return ResponseEntity.ok().build();
    }
}
