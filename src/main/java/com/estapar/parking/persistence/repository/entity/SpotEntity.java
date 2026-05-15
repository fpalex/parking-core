package com.estapar.parking.persistence.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "spot")
public class SpotEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    private SectorEntity sector;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Column(nullable = false)
    private Boolean occupied = false;

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