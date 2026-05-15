package com.estapar.parking.mapper;

import com.estapar.parking.domain.model.Spot;
import com.estapar.parking.persistence.repository.entity.SpotEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SectorMapper.class})
public interface SpotMapper {
    Spot toDomain(SpotEntity entity);
    SpotEntity toEntity(Spot domain);
}