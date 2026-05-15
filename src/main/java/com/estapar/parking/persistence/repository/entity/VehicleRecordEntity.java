package com.estapar.parking.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vehicle_record")
public class VehicleRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @ManyToOne
    @JoinColumn(name = "spot_id")
    private SpotEntity spot;

    @Column(nullable = false)
    private BigDecimal priceMultiplier = BigDecimal.ONE;

    private BigDecimal priceCharged;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}