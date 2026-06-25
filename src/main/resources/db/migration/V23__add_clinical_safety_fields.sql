-- V23: Adverse-event grading, optimistic locking, AI baseline tracking,
-- and the administrative/operational status split for tracing tasks.

ALTER TABLE home_visits
    ADD COLUMN adverse_event_grade INTEGER
        CHECK (adverse_event_grade IS NULL OR adverse_event_grade BETWEEN 1 AND 4),
    ADD COLUMN referral_initiated  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN record_version      INTEGER NOT NULL DEFAULT 0;

ALTER TABLE confirmation_logs
    ADD COLUMN baseline_established BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE ai_risk_scores
    ADD COLUMN baseline_observation_count INTEGER NOT NULL DEFAULT 0;

-- administrative_classification records the Rwanda-MOH LTFU category
-- (90-day cohort reporting) independently of the operational `status`
-- column, which tracks the PEPFAR/IIT-aligned tracing workflow (28-day
-- standard). The two clocks diverge by design — see LtfuScheduler.
ALTER TABLE tracing_tasks
    ADD COLUMN administrative_classification VARCHAR(20)
        CHECK (administrative_classification IS NULL OR administrative_classification IN
            ('ON_TIME', 'LATE', 'LOST_TO_FOLLOW_UP'));
