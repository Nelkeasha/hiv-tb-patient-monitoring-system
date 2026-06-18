-- V13: Add visit_status column to home_visits
-- Existing rows default to ATTENDED_TO (they were already saved = completed)
ALTER TABLE home_visits
    ADD COLUMN IF NOT EXISTS visit_status VARCHAR(20) NOT NULL DEFAULT 'ATTENDED_TO';
