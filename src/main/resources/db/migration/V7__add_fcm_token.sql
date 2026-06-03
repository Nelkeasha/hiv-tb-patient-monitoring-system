-- V7: Add FCM token column to system_users for Firebase push notifications
-- Nullable — populated when the user logs in on a device with FCM enabled.
-- Multiple devices per user are not supported in v1; the column stores
-- the most recent device token.
ALTER TABLE system_users
    ADD COLUMN fcm_token VARCHAR(255);

CREATE INDEX idx_system_users_fcm_token ON system_users(fcm_token)
    WHERE fcm_token IS NOT NULL;
