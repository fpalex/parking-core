package com.estapar.parking.service;

import com.estapar.parking.domain.model.RevenueFilter;
import com.estapar.parking.domain.model.RevenueResult;

public interface RevenueService {
    RevenueResult getRevenue(RevenueFilter filter);
}