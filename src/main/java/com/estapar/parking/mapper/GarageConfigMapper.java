package com.estapar.parking.mapper;

import com.estapar.parking.domain.model.GarageConfig;
import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.integration.client.dto.SimulatorGarageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GarageConfigMapper {

    @Mapping(target = "sectors", source = "garage")
    @Mapping(target = "spots", source = "spots")
    GarageConfig toDomain(SimulatorGarageResponse response);

    @Mapping(target = "name", source = "sector")
    @Mapping(target = "currentOccupancy", constant = "0")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Sector sectorDtoToDomain(SimulatorGarageResponse.SectorDto dto);

    @Mapping(target = "sector.name", source = "sector")
    @Mapping(target = "occupied", constant = "false")
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Spot spotDtoToDomain(SimulatorGarageResponse.SpotDto dto);
}