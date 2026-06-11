-- V10: Track which user resolved an alert, and 48h escalation tracking
ALTER TABLE alerts
    ADD COLUMN resolved_by UUID REFERENCES system_users(id),
    ADD COLUMN escalated_at TIMESTAMP;
