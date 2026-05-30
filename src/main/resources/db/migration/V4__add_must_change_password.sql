-- V4: Track whether a user must change their temp password on first login
ALTER TABLE system_users
    ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT FALSE;
