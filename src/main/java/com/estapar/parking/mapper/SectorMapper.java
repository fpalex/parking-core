package com.estapar.parking.mapper;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.persistence.repository.entity.SectorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SectorMapper {
    Sector toDomain(SectorEntity entity);
    SectorEntity toEntity(Sector domain);
}