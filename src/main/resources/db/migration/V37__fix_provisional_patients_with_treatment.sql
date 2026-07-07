-- V37: One-time seed cleanup — reconcile PROVISIONAL records that already own a
-- treatment plan.
--
-- Under the data-separation model, a PROVISIONAL record is a screening voucher and
-- must NOT have chronic-tracking data: TreatmentPlanService.createPlan() already
-- rejects any patient whose status isn't CONFIRMED. Yet some demo/seed rows were
-- written directly to the DB as PROVISIONAL while carrying a full treatment history
-- (plans, dose schedules, confirmations, alerts, risk scores) — the contradiction
-- seen on the clinical board (a patient "awaiting confirmation" with missed-dose
-- and high-risk alerts).
--
-- A record with an actual treatment plan is, by definition, a patient already under
-- treatment — i.e. it should be CONFIRMED. Promote exactly those rows so their
-- status matches reality; genuine vouchers (no plan) are left untouched in the queue.

UPDATE patients p
SET registration_status = 'CONFIRMED',
    is_active           = true,
    confirmed_at        = COALESCE(p.confirmed_at, NOW())
WHERE p.registration_status = 'PROVISIONAL'
  AND EXISTS (SELECT 1 FROM treatment_plans tp WHERE tp.patient_id = p.id);
