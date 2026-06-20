-- V18: lab_results — stores lab Observation values pulled from the FHIR
-- server (e.g. HIV viral load, CD4 count), keyed by patient + LOINC code,
-- one row per observation date. Written and read by the Python AI service
-- only (clinical_correlation_service.py compares the trend here against
-- self-reported adherence — REQ-13). No Spring Boot entity reads/writes
-- this table directly; it exists here because schema is Flyway-managed
-- for the whole shared database, not because the Java side uses it.
CREATE TABLE lab_results (
    id                  UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id          UUID        NOT NULL REFERENCES patients(id),
    loinc_code          VARCHAR(20) NOT NULL,
    value               NUMERIC(12, 4) NOT NULL,
    unit                VARCHAR(30),
    observed_at         TIMESTAMP   NOT NULL,
    fhir_observation_id VARCHAR(100),
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_lab_results_patient_loinc_date
    ON lab_results (patient_id, loinc_code, observed_at DESC);

-- One row per patient/code/observation date — re-running the FHIR poll is safe.
CREATE UNIQUE INDEX idx_lab_results_patient_loinc_observed_at
    ON lab_results (patient_id, loinc_code, observed_at);
