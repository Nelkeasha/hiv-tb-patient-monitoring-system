-- V35: One-time cleanup — collapse duplicate OPEN missed-dose alerts to one per patient.
--
-- Before AlertService.createMissedDoseAlert() was made update-in-place, the
-- missed-dose scheduler inserted a fresh MISSED_DOSE alert for a patient every
-- day their streak continued, so an ongoing streak produced many near-identical
-- open rows (the "duplicated alerts" seen on the clinical board).
--
-- Keep only the most recent open MISSED_DOSE alert per patient (it represents the
-- current streak) and DELETE the older stale snapshots. We delete rather than
-- resolve them: they were never actually acted on, so marking them "resolved"
-- would pollute the Resolved history with fake resolutions. This runs once;
-- going forward the single living alert is updated in place.

DELETE FROM alerts a
USING (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY patient_id
               ORDER BY created_at DESC, id
           ) AS rn
    FROM alerts
    WHERE alert_type = 'MISSED_DOSE'
      AND is_resolved = false
      AND patient_id IS NOT NULL
) dup
WHERE a.id = dup.id
  AND dup.rn > 1;
