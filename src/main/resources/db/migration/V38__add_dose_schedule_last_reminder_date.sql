-- Tracks the last calendar day a reminder was sent for each dose schedule, so
-- the reminder schedulers can fire anywhere inside the dose window (robust to a
-- missed scheduler tick / instance sleep) while still sending at most one
-- reminder per schedule per day.
ALTER TABLE dose_schedules ADD COLUMN IF NOT EXISTS last_reminder_date DATE;
