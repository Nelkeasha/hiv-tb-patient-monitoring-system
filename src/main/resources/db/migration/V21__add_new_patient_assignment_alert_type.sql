-- V21: Add NEW_PATIENT_ASSIGNMENT to alert_type enum — raised when a
-- self-presenting patient is auto/manually matched to a CHW by village,
-- and again (with full detail) if the CHW does not accept within 48h.
ALTER TYPE alert_type ADD VALUE IF NOT EXISTS 'NEW_PATIENT_ASSIGNMENT';
