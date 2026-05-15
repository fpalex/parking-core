package com.estapar.parking.service.impl;

import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.exception.BusinessException;
import com.estapar.parking.service.WebhookService;
import com.estapar.parking.service.strategy.WebhookEventStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebhookServiceImpl implements WebhookService {

    private final Map<String, WebhookEventStrategy> strategyMap;

    public WebhookServiceImpl(final List<WebhookEventStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        WebhookEventStrategy::getEventType,
                        strategy -> strategy));
        log.info("Registered webhook strategies: {}", strategyMap.keySet());
    }

    @Override
    public void handle(final WebhookEvent event) {
        WebhookEventStrategy strategy = strategyMap.get(event.getEventType());

        if (strategy == null) {
            throw new BusinessException("Unknown event type: " + event.getEventType());
        }

        strategy.handle(event);
    }
}