package com.estapar.parking.integration.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SimulatorGarageResponse {

    @JsonProperty("garage")
    private List<SectorDto> garage;

    @JsonProperty("spots")
    private List<SpotDto> spots;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SectorDto {

        private String sector;

        @JsonProperty("base_price")
        private BigDecimal basePrice;

        @JsonProperty("max_capacity")
        private Integer maxCapacity;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpotDto {
        private Long id;
        private String sector;
        private Double lat;
        private Double lng;
    }
}