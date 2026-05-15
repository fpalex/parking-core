package com.estapar.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ParkingCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingCoreApplication.class, args);
    }
}