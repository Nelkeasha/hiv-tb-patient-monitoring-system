-- ENUM types for referrals
CREATE TYPE referral_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'MODIFIED',
    'ATTENDED',
    'NOT_ATTENDED',
    'CANCELLED'
);

CREATE TYPE referral_urgency AS ENUM (
    'ROUTINE',
    'URGENT',
    'EMERGENCY'
);

-- Referrals table
CREATE TABLE referrals (
    id                      UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id              UUID NOT NULL REFERENCES patients(id),
    referred_by_chw_id      UUID NOT NULL REFERENCES chws(id),
    confirmed_by_provider_id UUID REFERENCES facility_providers(id),
    referral_date           DATE NOT NULL,
    referral_reason         TEXT NOT NULL,
    urgency                 referral_urgency NOT NULL,
    status                  referral_status NOT NULL DEFAULT 'PENDING',
    facility_appointment_date DATE,
    provider_notes          TEXT,
    attendance_notes        TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_referrals_patient_id ON referrals(patient_id);
CREATE INDEX idx_referrals_chw_id ON referrals(referred_by_chw_id);
CREATE INDEX idx_referrals_status ON referrals(status);
