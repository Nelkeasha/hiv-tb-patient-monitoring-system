-- V2: Add dose_schedules, align treatment_plans, confirmation_logs, medication_records
--     with updated thesis data dictionary (Tables 15, 17, 23, 24)

-- ============================================================
-- 1. TREATMENT_PLANS — replace single regimen field with
--    medication_name + dosage + frequency; swap provider_id
--    for created_by (system_users); add sync_status
-- ============================================================
ALTER TABLE treatment_plans
    ADD COLUMN medication_name VARCHAR(100),
    ADD COLUMN dosage           VARCHAR(50),
    ADD COLUMN frequency        VARCHAR(50),
    ADD COLUMN sync_status      sync_status DEFAULT 'PENDING',
    ADD COLUMN created_by       UUID REFERENCES system_users(id);

ALTER TABLE treatment_plans DROP COLUMN regimen;
ALTER TABLE treatment_plans DROP COLUMN provider_id;
ALTER TABLE treatment_plans DROP COLUMN notes;

ALTER TABLE treatment_plans
    ALTER COLUMN medication_name SET NOT NULL,
    ALTER COLUMN dosage           SET NOT NULL,
    ALTER COLUMN frequency        SET NOT NULL;

-- ============================================================
-- 2. DOSE_SCHEDULES — new table (Table 23)
--    Stores specific dose times per treatment plan, drives
--    the notification dispatch and confirmation window system
-- ============================================================
CREATE TABLE dose_schedules (
    id                       UUID    PRIMARY KEY DEFAULT uuid_generate_v4(),
    plan_id                  UUID    NOT NULL REFERENCES treatment_plans(id),
    patient_id               UUID    NOT NULL REFERENCES patients(id),
    dose_time                TIME    NOT NULL,
    dose_label               VARCHAR(50),
    notification_method      confirmation_channel DEFAULT 'APP',
    window_duration_minutes  INTEGER DEFAULT 45,
    is_active                BOOLEAN DEFAULT TRUE,
    created_by               UUID    REFERENCES system_users(id),
    created_at               TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_dose_schedules_plan_id    ON dose_schedules(plan_id);
CREATE INDEX idx_dose_schedules_patient_id ON dose_schedules(patient_id);

-- ============================================================
-- 3. CONFIRMATION_LOGS — align with Table 24
--    Drop status enum column; replace with boolean flags
--    that the AI engine writes directly
-- ============================================================
ALTER TABLE confirmation_logs DROP COLUMN scheduled_dose_time;
ALTER TABLE confirmation_logs DROP COLUMN confirmation_time;
ALTER TABLE confirmation_logs DROP COLUMN status;

DROP TYPE confirmation_status;

ALTER TABLE confirmation_logs RENAME COLUMN channel TO confirmation_method;

ALTER TABLE confirmation_logs
    ADD COLUMN plan_id          UUID      REFERENCES treatment_plans(id),
    ADD COLUMN schedule_id      UUID      REFERENCES dose_schedules(id),
    ADD COLUMN scheduled_date   DATE,
    ADD COLUMN confirmed_at     TIMESTAMP,
    ADD COLUMN raw_sms_response VARCHAR(20),
    ADD COLUMN is_missed        BOOLEAN   NOT NULL DEFAULT FALSE,
    ADD COLUMN ai_suspicion_flag BOOLEAN  NOT NULL DEFAULT FALSE,
    ADD COLUMN suspicion_reason VARCHAR(100);

ALTER TABLE confirmation_logs ALTER COLUMN scheduled_date SET NOT NULL;

-- ============================================================
-- 4. MEDICATION_RECORDS — align with Table 17
--    Link to treatment plan instead of CHW; split doses_taken
--    into doses_confirmed (patient) + doses_verified (CHW pill count)
-- ============================================================
ALTER TABLE medication_records DROP COLUMN chw_id;
ALTER TABLE medication_records DROP COLUMN medication_name;
ALTER TABLE medication_records DROP COLUMN doses_taken;
ALTER TABLE medication_records DROP COLUMN fhir_resource_id;
ALTER TABLE medication_records DROP COLUMN calculated_at;

ALTER TABLE medication_records
    ADD COLUMN plan_id                UUID      REFERENCES treatment_plans(id),
    ADD COLUMN doses_confirmed        INTEGER,
    ADD COLUMN doses_verified         INTEGER,
    ADD COLUMN false_confirmation_flag BOOLEAN  DEFAULT FALSE,
    ADD COLUMN fhir_statement_id      VARCHAR(100),
    ADD COLUMN updated_at             TIMESTAMP DEFAULT NOW();

ALTER TABLE medication_records
    ALTER COLUMN doses_confirmed SET NOT NULL,
    ALTER COLUMN doses_verified  SET NOT NULL;
