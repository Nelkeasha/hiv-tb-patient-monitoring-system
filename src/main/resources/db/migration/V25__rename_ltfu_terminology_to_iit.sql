-- ============================================================
-- V25: Align LTFU tracing terminology with PEPFAR/IIT language
--   tracing_tasks.status : CHW_ASSIGNED   -> IIT_ESCALATED
--                           LTFU_CONFIRMED -> TREATMENT_INTERRUPTED
--   alert_type            : LTFU_TRACING   -> IIT_ESCALATED
--                           LTFU_CONFIRMED -> TREATMENT_INTERRUPTED
-- The Rwanda-MOH administrative_classification column (V23) is
-- untouched — it intentionally keeps its own LOST_TO_FOLLOW_UP
-- vocabulary for 90-day cohort reporting, independent of this
-- operational 28-day tracing workflow.
-- ============================================================

-- ── tracing_tasks.status ──────────────────────────────────────
-- Column was VARCHAR(20); 'TREATMENT_INTERRUPTED' is 22 chars, so it must be
-- widened before the UPDATE below or Postgres rejects the value outright.
ALTER TABLE tracing_tasks ALTER COLUMN status TYPE VARCHAR(30);

ALTER TABLE tracing_tasks DROP CONSTRAINT IF EXISTS tracing_tasks_status_check;

UPDATE tracing_tasks SET status = 'IIT_ESCALATED'      WHERE status = 'CHW_ASSIGNED';
UPDATE tracing_tasks SET status = 'TREATMENT_INTERRUPTED' WHERE status = 'LTFU_CONFIRMED';

ALTER TABLE tracing_tasks ADD CONSTRAINT tracing_tasks_status_check
    CHECK (status IN ('LATE', 'IIT_ESCALATED', 'RESOLVED', 'TREATMENT_INTERRUPTED', 'ESCALATED'));

-- ── alert_type enum ───────────────────────────────────────────
ALTER TABLE alerts ALTER COLUMN alert_type TYPE VARCHAR(50);

UPDATE alerts SET alert_type = 'IIT_ESCALATED'         WHERE alert_type = 'LTFU_TRACING';
UPDATE alerts SET alert_type = 'TREATMENT_INTERRUPTED' WHERE alert_type = 'LTFU_CONFIRMED';

DROP TYPE IF EXISTS alert_type;

CREATE TYPE alert_type AS ENUM (
    'MISSED_DOSE',
    'FALSE_CONFIRMATION',
    'CLINICAL_DISCREPANCY',
    'EARLY_WARNING',
    'IIT_ESCALATED',
    'TREATMENT_INTERRUPTED',
    'LTFU_TRACING_RESOLVED',
    'SYNC_FAILURE',
    'NEW_PATIENT_ASSIGNMENT'
);

ALTER TABLE alerts
    ALTER COLUMN alert_type TYPE alert_type
    USING alert_type::alert_type;
