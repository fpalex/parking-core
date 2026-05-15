package com.estapar.parking.service;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.domain.model.WebhookEvent;

import java.math.BigDecimal;

public interface PricingService {
    BigDecimal calculateMultiplier(Sector sector);
    BigDecimal calculatePrice(VehicleRecord record, WebhookEvent event, Sector sector);
}