-- V20: CHW assignment acceptance gate — when a patient self-presents at the
-- facility (registration_route = 'FACILITY'), the assigned CHW (found by
-- matching the patient's village/sector against chws.assigned_village/
-- assigned_sector) must accept the assignment before the full patient
-- record is visible to them. Existing patients default to ACCEPTED since
-- they predate this gate.
ALTER TABLE patients
    ADD COLUMN chw_assignment_status VARCHAR(20) NOT NULL DEFAULT 'ACCEPTED',
    ADD COLUMN chw_assigned_at TIMESTAMP,
    ADD COLUMN chw_accepted_at TIMESTAMP,
    ADD COLUMN chw_assignment_reminder_sent_at TIMESTAMP,
    ADD COLUMN chw_assignment_escalated_at TIMESTAMP;

CREATE INDEX idx_patients_chw_assignment_status ON patients (chw_assignment_status) WHERE chw_assignment_status = 'PENDING';
