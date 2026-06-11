ALTER TABLE system_users ADD COLUMN failed_login_attempts INT DEFAULT 0;
ALTER TABLE system_users ADD COLUMN account_locked BOOLEAN DEFAULT FALSE;
