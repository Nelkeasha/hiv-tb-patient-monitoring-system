-- V11: Add LTFU_TRACING_RESOLVED to alert_type enum (3.4.4 — facility provider
-- notification when a tracing task is resolved)
ALTER TYPE alert_type ADD VALUE IF NOT EXISTS 'LTFU_TRACING_RESOLVED';
