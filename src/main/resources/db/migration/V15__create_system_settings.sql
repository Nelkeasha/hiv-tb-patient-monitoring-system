-- Single global row of admin-configurable thresholds, previously hardcoded
-- in scattered places (Java DTO defaults, Python AI service env vars) with
-- no way for the System Administrator to actually change them.
CREATE TABLE system_settings (
    id                       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    missed_dose_threshold    INTEGER NOT NULL DEFAULT 2,
    low_stock_days           INTEGER NOT NULL DEFAULT 14,
    confirm_window_minutes   INTEGER NOT NULL DEFAULT 45,
    high_risk_threshold      INTEGER NOT NULL DEFAULT 70,
    critical_risk_threshold  INTEGER NOT NULL DEFAULT 85,
    updated_at               TIMESTAMP DEFAULT NOW()
);

INSERT INTO system_settings DEFAULT VALUES;
