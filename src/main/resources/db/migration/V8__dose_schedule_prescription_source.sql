-- V8: Add prescription_source to dose_schedules
--     Records the clinical note or clinic card reference that the
--     schedule was based on. Filled by clinical staff at schedule creation
--     or when adjusting an existing schedule.

ALTER TABLE dose_schedules
    ADD COLUMN prescription_source TEXT;
