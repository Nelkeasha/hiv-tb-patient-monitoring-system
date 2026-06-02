-- ============================================================
-- V6: LTFU Tracing Tasks + Enum Cleanup + Remove Stock Tables
-- Implements thesis requirements: Update 1 (remove stock),
-- Update 2 (tracing_tasks), Update 5 (alert_type updates)
-- ============================================================

-- ============================================================
-- 1. REMOVE STOCK MANAGEMENT TABLES (Update 1)
--    Drop dispensing_events first (FK → stock_records)
-- ============================================================
DROP TABLE IF EXISTS dispensing_events CASCADE;
DROP TABLE IF EXISTS stock_records CASCADE;

-- ============================================================
-- 2. UPDATE alert_type ENUM (Update 1 + Update 2)
--    Remove: LOW_ADHERENCE, LOW_STOCK, MISSED_VISIT
--    Add: LTFU_TRACING, LTFU_CONFIRMED
--    Final set: MISSED_DOSE, FALSE_CONFIRMATION,
--               CLINICAL_DISCREPANCY, EARLY_WARNING,
--               LTFU_TRACING, LTFU_CONFIRMED
-- ============================================================

-- Step 2a: Convert column to text so we can drop the enum
ALTER TABLE alerts ALTER COLUMN alert_type TYPE VARCHAR(50);

-- Step 2b: Remap obsolete values to nearest valid equivalent
UPDATE alerts
SET alert_type = 'MISSED_DOSE'
WHERE alert_type IN ('LOW_ADHERENCE', 'LOW_STOCK', 'MISSED_VISIT', 'STOCKOUT');

-- Step 2c: Drop the old enum type
DROP TYPE IF EXISTS alert_type;

-- Step 2d: Recreate enum with thesis-correct values
CREATE TYPE alert_type AS ENUM (
    'MISSED_DOSE',
    'FALSE_CONFIRMATION',
    'CLINICAL_DISCREPANCY',
    'EARLY_WARNING',
    'LTFU_TRACING',
    'LTFU_CONFIRMED'
);

-- Step 2e: Reapply enum type to column
ALTER TABLE alerts
    ALTER COLUMN alert_type TYPE alert_type
    USING alert_type::alert_type;

-- ============================================================
-- 3. ADD CLINICAL_STAFF AND ADMIN TO user_role ENUM
--    Keeps FACILITY_PROVIDER and SUPERVISOR for backward compat
-- ============================================================
ALTER TYPE user_role ADD VALUE IF NOT EXISTS 'CLINICAL_STAFF';
ALTER TYPE user_role ADD VALUE IF NOT EXISTS 'ADMIN';

-- ============================================================
-- 4. CREATE tracing_tasks TABLE (Update 2 — thesis Table 19)
--    Tracks each CHW's LTFU tracing lifecycle from LATE
--    through CHW_ASSIGNED to LTFU_CONFIRMED per Rwanda MOH
--    national LTFU protocol (30-day threshold)
-- ============================================================
CREATE TABLE tracing_tasks (
    id                      UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id              UUID        NOT NULL REFERENCES patients(id),
    chw_id                  UUID        NOT NULL REFERENCES chws(id),
    missed_appointment_date DATE        NOT NULL,
    days_since_missed       INTEGER     NOT NULL DEFAULT 0,
    reason                  VARCHAR(30) NOT NULL
        CHECK (reason IN ('MISSED_REFILL', 'MISSED_APPOINTMENT', 'LOST_TO_FOLLOWUP')),
    status                  VARCHAR(20) NOT NULL DEFAULT 'LATE'
        CHECK (status IN ('LATE', 'CHW_ASSIGNED', 'RESOLVED', 'LTFU_CONFIRMED', 'ESCALATED')),
    ltfu_confirmed_at       TIMESTAMP,
    outcome                 VARCHAR(30)
        CHECK (outcome IS NULL OR outcome IN (
            'PATIENT_FOUND', 'PATIENT_REFUSED', 'PATIENT_HOSPITALIZED',
            'PROXY_AUTHORIZED', 'UNABLE_TO_LOCATE'
        )),
    disengagement_reason    VARCHAR(30)
        CHECK (disengagement_reason IS NULL OR disengagement_reason IN (
            'STIGMA', 'TRANSPORT_COST', 'SIDE_EFFECTS',
            'FEELING_HEALTHY', 'WORK_RELOCATION', 'FAMILY_ISSUES', 'OTHER'
        )),
    resolution_plan         TEXT,
    proxy_authorized        BOOLEAN     NOT NULL DEFAULT FALSE,
    proxy_name              VARCHAR(100),
    notes                   TEXT,
    escalated_to            UUID        REFERENCES system_users(id),
    created_at              TIMESTAMP   NOT NULL DEFAULT NOW(),
    resolved_at             TIMESTAMP
);

CREATE INDEX idx_tracing_tasks_patient_id  ON tracing_tasks(patient_id);
CREATE INDEX idx_tracing_tasks_chw_id      ON tracing_tasks(chw_id);
CREATE INDEX idx_tracing_tasks_status      ON tracing_tasks(status);
CREATE INDEX idx_tracing_tasks_created_at  ON tracing_tasks(created_at);

-- Prevent duplicate open tasks for the same patient + appointment date
CREATE UNIQUE INDEX idx_tracing_tasks_patient_date_open
    ON tracing_tasks(patient_id, missed_appointment_date)
    WHERE status NOT IN ('RESOLVED');
