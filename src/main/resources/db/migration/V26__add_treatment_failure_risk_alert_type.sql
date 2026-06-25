-- V26: Reserve TREATMENT_FAILURE_RISK in the alert_type enum.
-- Not yet produced by any service — real treatment-failure detection
-- requires viral-load/lab results, which this system does not currently
-- ingest (no FHIR DiagnosticReport/Observation source is polled). Added
-- now so the wire format is stable once that data source exists; until
-- then no code emits this alert type.
ALTER TYPE alert_type ADD VALUE IF NOT EXISTS 'TREATMENT_FAILURE_RISK';
