-- The confirmation window is a single system-wide value (45 minutes), applied to
-- every dose schedule regardless of dose time. Existing schedules created with a
-- custom window (e.g. 60) are reset, and the settings knob is put back to 45.
UPDATE dose_schedules SET window_duration_minutes = 45
WHERE window_duration_minutes IS DISTINCT FROM 45;

UPDATE system_settings SET confirm_window_minutes = 45;

ALTER TABLE dose_schedules ALTER COLUMN window_duration_minutes SET DEFAULT 45;
