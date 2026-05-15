package com.estapar.parking.factory;

import com.estapar.parking.domain.model.Sector;
import com.estapar.parking.domain.model.Spot;

public class SpotFactory {

    public static Spot build(Sector sector) {
        return build(sector, false);
    }

    public static Spot build(Sector sector, boolean occupied) {
        return Spot.builder()
                .id(1L)
                .sector(sector)
                .lat(-23.561684)
                .lng(-46.655981)
                .occupied(occupied)
                .build();
    }
}