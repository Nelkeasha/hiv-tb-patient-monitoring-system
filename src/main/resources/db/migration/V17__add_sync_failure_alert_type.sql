-- V17: Add SYNC_FAILURE to alert_type enum — raised when a CHW's or patient's
-- offline-queued action (home visit, dose confirmation) is permanently
-- rejected by the server (e.g. duplicate confirmation) and cannot be retried.
ALTER TYPE alert_type ADD VALUE IF NOT EXISTS 'SYNC_FAILURE';
