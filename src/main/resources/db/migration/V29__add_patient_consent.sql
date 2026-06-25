-- V29: Patient-level data-collection consent (Rwanda Law No. 058/2021).
--
-- The system_users.consent_given column added in V22 captures app-account
-- holders (CHW/patient login) agreeing to app terms — that is a different
-- thing from this. This column captures the PATIENT (who may never log into
-- anything themselves) consenting, in person, to the CHW collecting and
-- digitally storing their HIV/TB data at the moment of registration. Without
-- this, storing special-category health data has no documented consent.

ALTER TABLE patients
    ADD COLUMN consent_given     BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN consent_timestamp TIMESTAMP,
    ADD COLUMN consent_version   VARCHAR(20);

-- Existing rows predate this requirement and have no real consent record to
-- backfill — leaving them at the FALSE default would be dishonest in the
-- other direction (claiming non-consent that was never actually solicited).
-- Mark them with a synthetic "legacy" version so they're visibly distinct
-- from patients who went through the real consent step, rather than either
-- silently granting consent or silently blocking pre-existing patients.
UPDATE patients
    SET consent_given = TRUE,
        consent_timestamp = created_at,
        consent_version = 'LEGACY_PRE_V29'
    WHERE consent_given = FALSE;
