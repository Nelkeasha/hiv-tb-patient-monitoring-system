-- AI confirmation analysis can combine multiple signals into one message,
-- which routinely exceeds the original 100-char cap.
ALTER TABLE confirmation_logs ALTER COLUMN suspicion_reason TYPE VARCHAR(500);
