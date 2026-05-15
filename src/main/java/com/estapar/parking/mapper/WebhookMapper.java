package com.estapar.parking.mapper;

import com.estapar.parking.domain.model.WebhookEvent;
import com.estapar.parking.dto.WebhookEventDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WebhookMapper {
    WebhookEvent toDomain(WebhookEventDto dto);
}