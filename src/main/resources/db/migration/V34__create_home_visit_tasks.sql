-- V34: Triggered home-visit tasks (Part 3 of the differentiated DOT model)
--
-- Daily in-person DOT is not required. An in-person home visit becomes an explicit
-- CHW task ONLY when a real trigger fires, each carrying the reason so the CHW knows
-- why the visit is needed. Routine swallow verification is Video-DOT (future work).

CREATE TABLE home_visit_tasks (
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id         UUID NOT NULL REFERENCES patients(id),
    chw_id             UUID NOT NULL REFERENCES chws(id),
    -- MISSED_DOSES | SIDE_EFFECT | IIT_ESCALATED | HIGH_RISK | PERIODIC_REVIEW | INITIAL_ASSESSMENT
    trigger_type       VARCHAR(30) NOT NULL,
    reason             VARCHAR(255),
    status             VARCHAR(20) NOT NULL DEFAULT 'OPEN',   -- OPEN | COMPLETED
    created_at         TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at       TIMESTAMP,
    completed_visit_id UUID
);

CREATE INDEX idx_hvt_chw_status ON home_visit_tasks (chw_id, status);

-- Idempotency: at most one OPEN task per patient per trigger, so repeated firing
-- of the same trigger doesn't spam the CHW's list with duplicates.
CREATE UNIQUE INDEX uq_hvt_open_patient_trigger
    ON home_visit_tasks (patient_id, trigger_type)
    WHERE status = 'OPEN';
