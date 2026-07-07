-- V36: Screen-negative resolution (RBC 2022 data-separation boundary).
--
-- A CHW screening creates a PROVISIONAL record — effectively a Referral Voucher in
-- the tracking queue (patients.referral_id). It is NOT a confirmed active patient:
-- every active/chronic view (facility patient list, TX_CURR-style active lists, the
-- DOT calendar, AI risk scoring, FHIR sync) is already gated on registration_status
-- = 'CONFIRMED'. A person only enters those tables after lab confirmation.
--
-- This adds the terminal state for a NEGATIVE lab result: the voucher is flagged
-- RESOLVED_NEGATIVE, deactivated, and dropped from the provisional queue — the
-- "registry block" that keeps unverified cases out of the clinic's performance data.
-- The case is then redirected to prevention (CHW tasks: TB differential / HIV PrEP).

ALTER TABLE patients DROP CONSTRAINT IF EXISTS chk_patients_registration_status;
ALTER TABLE patients ADD CONSTRAINT chk_patients_registration_status
    CHECK (registration_status IN ('PROVISIONAL', 'CONFIRMED', 'RESOLVED_NEGATIVE'));
