-- V27: Add ADVERSE_EVENT to the alert_type enum — fired when a CHW records
-- a home visit with a CTCAE Grade 3 or 4 adverse_event_grade (V23).
ALTER TYPE alert_type ADD VALUE IF NOT EXISTS 'ADVERSE_EVENT';
