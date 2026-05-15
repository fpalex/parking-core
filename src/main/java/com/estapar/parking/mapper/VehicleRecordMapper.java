package com.estapar.parking.mapper;

import com.estapar.parking.domain.model.VehicleRecord;
import com.estapar.parking.persistence.repository.entity.VehicleRecordEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SpotMapper.class})
public interface VehicleRecordMapper {
    VehicleRecord toDomain(VehicleRecordEntity entity);
    VehicleRecordEntity toEntity(VehicleRecord domain);
}