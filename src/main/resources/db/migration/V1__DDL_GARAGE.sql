-- ============================================================================= --
-- Initial schema creation for the parking garage management system.             --
-- Creates tables for sectors, spots and vehicle records.                        --
-- ============================================================================= --
CREATE TABLE sector (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(10)    NOT NULL UNIQUE,
    base_price        DECIMAL(10, 2) NOT NULL,
    max_capacity      INT            NOT NULL,
    current_occupancy INT            NOT NULL DEFAULT 0,
    open_hour         VARCHAR(5)     NOT NULL DEFAULT '00:00',
    close_hour        VARCHAR(5)     NOT NULL DEFAULT '23:59',
    duration_limit_minutes INT,
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME
);

CREATE TABLE spot (
    id          BIGINT         PRIMARY KEY,
    sector_id   BIGINT         NOT NULL,
    lat         DOUBLE         NOT NULL,
    lng         DOUBLE         NOT NULL,
    occupied    BOOLEAN        NOT NULL DEFAULT FALSE,
    is_active   BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME,
    FOREIGN KEY (sector_id) REFERENCES sector(id)
);

CREATE TABLE vehicle_record (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate    VARCHAR(20)    NOT NULL,
    entry_time       DATETIME       NOT NULL,
    exit_time        DATETIME,
    spot_id          BIGINT,
    price_multiplier DECIMAL(5, 2)  NOT NULL DEFAULT 1.00,
    price_charged    DECIMAL(10, 2),
    is_active        BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME,
    FOREIGN KEY (spot_id) REFERENCES spot(id)
);