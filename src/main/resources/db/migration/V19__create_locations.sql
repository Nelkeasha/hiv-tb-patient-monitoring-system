-- V19: locations — administrative geography hierarchy (province/district/
-- sector/cell/village), self-referencing via parent_id. Used to look up
-- which village/sector a self-presenting patient belongs to and match it
-- against a CHW's assigned coverage area (chws.assigned_village/assigned_sector).
CREATE TABLE locations (
    id            BIGSERIAL    PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    code          VARCHAR(10)  NOT NULL UNIQUE,
    description   VARCHAR(500),
    location_type VARCHAR(20)  NOT NULL CHECK (location_type IN ('PROVINCE', 'DISTRICT', 'SECTOR', 'CELL', 'VILLAGE')),
    parent_id     BIGINT       REFERENCES locations(id),
    population    BIGINT,
    area_km2      DOUBLE PRECISION,
    village_chief VARCHAR(255)
);

CREATE INDEX idx_locations_parent_id ON locations (parent_id);
CREATE INDEX idx_locations_location_type ON locations (location_type);
