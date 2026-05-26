-- V3: Link patients to system_users for JWT authentication
--     Patients authenticate via the same system_users table as other roles.
--     user_id is nullable to preserve existing patient rows.

ALTER TABLE patients
    ADD COLUMN user_id UUID REFERENCES system_users(id);

CREATE INDEX idx_patients_user_id ON patients(user_id);
