-- V9: Two-Route Patient Registration Model
--   Route A — FACILITY:      Clinical staff registers confirmed patient (ACTIVE)
--   Route B — CHW_SCREENING: CHW screens suspected case in the field (PROVISIONAL)
--                            Facility confirms and upgrades to ACTIVE

ALTER TABLE patients
    ADD COLUMN registration_route   VARCHAR(20) NOT NULL DEFAULT 'FACILITY',
    ADD COLUMN registration_status  VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN referral_id          VARCHAR(30) UNIQUE,
    ADD COLUMN screened_by_chw_id   UUID REFERENCES chws(id),
    ADD COLUMN screened_at          TIMESTAMP,
    ADD COLUMN confirmed_by         UUID REFERENCES system_users(id),
    ADD COLUMN confirmed_at         TIMESTAMP,
    ADD COLUMN suspected_condition  VARCHAR(50),
    ADD COLUMN screening_symptoms   TEXT,
    ADD COLUMN screening_notes      TEXT,
    ADD COLUMN lab_result_notes     TEXT,
    ADD COLUMN province             VARCHAR(100),
    ADD COLUMN cell                 VARCHAR(100);

-- All pre-existing patients were registered at the facility
UPDATE patients SET registration_route = 'FACILITY', registration_status = 'ACTIVE';

CREATE INDEX idx_patients_registration_status ON patients(registration_status);
CREATE INDEX idx_patients_referral_id ON patients(referral_id);
