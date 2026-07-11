-- V40: Add REFERRAL_CONFIRMED to alert_type enum — in-app notification to the
-- screening CHW when clinical staff confirm their provisional referral
-- (PROVISIONAL → CONFIRMED). Email/SMS are disabled in this deployment, so the
-- in-app alert (plus the WebSocket relay) is the reliable channel.
ALTER TYPE alert_type ADD VALUE IF NOT EXISTS 'REFERRAL_CONFIRMED';
