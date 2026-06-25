-- V22: Consent capture on system_users + geohash-based location on patients.
--
-- consent_given/consent_timestamp/consent_version let the app prove a user
-- (CHW or patient, registered via the mobile app) explicitly agreed to data
-- collection before any record referencing them is created.
--
-- location_geohash replaces the household_location free-text field as the
-- privacy-preserving substitute for exact coordinates. No lat/long ever
-- existed in this schema — geohash is computed client-side from a one-time
-- GPS read and only the encoded string (default precision 7, ~150m cell) is
-- stored, so the server never receives raw coordinates.

ALTER TABLE system_users
    ADD COLUMN consent_given     BOOLEAN     NOT NULL DEFAULT FALSE,
    ADD COLUMN consent_timestamp TIMESTAMP,
    ADD COLUMN consent_version   VARCHAR(20);

ALTER TABLE patients
    ADD COLUMN location_geohash VARCHAR(12);

-- registration_status has been VARCHAR(20) with no DB-level constraint since
-- V9; only 'PROVISIONAL' and 'CONFIRMED' are ever written by application
-- code (the 'ACTIVE' Java default is dead — always overwritten before
-- persist). Lock that down at the DB level now that it's load-bearing for
-- the clinical-confirmation gate.
UPDATE patients SET registration_status = 'PROVISIONAL'
    WHERE registration_status NOT IN ('PROVISIONAL', 'CONFIRMED') OR registration_status IS NULL;

ALTER TABLE patients
    ALTER COLUMN registration_status SET DEFAULT 'PROVISIONAL',
    ALTER COLUMN registration_status SET NOT NULL,
    ADD CONSTRAINT chk_patients_registration_status
        CHECK (registration_status IN ('PROVISIONAL', 'CONFIRMED'));
