package com.estapar.parking.mapper;

import com.estapar.parking.domain.model.RevenueFilter;
import com.estapar.parking.domain.model.RevenueResult;
import com.estapar.parking.dto.RevenueRequestDto;
import com.estapar.parking.dto.RevenueResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RevenueMapper {
    RevenueFilter toDomain(RevenueRequestDto dto);

    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "currency", constant = "BRL")
    RevenueResponseDto toDto(RevenueResult result);
}