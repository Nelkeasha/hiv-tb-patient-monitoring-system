-- V39: Provider-level (doctor-level) patient scoping. Every patient carries the
-- facility provider who manages their care; other providers at the same
-- facility cannot see, confirm, or manage that record (facility-level scoping
-- is unchanged and still applies first). NULL = no owning provider yet
-- (legacy rows, admin-registered) — visible to all providers at the facility.
ALTER TABLE patients ADD COLUMN IF NOT EXISTS managing_provider_id UUID REFERENCES facility_providers(id);
CREATE INDEX IF NOT EXISTS idx_patients_managing_provider ON patients(managing_provider_id);

-- Backfill: the provider who confirmed the patient becomes its managing
-- provider. confirmed_by stores a system_users id; map it through
-- facility_providers. Rows confirmed by admins (no provider profile) stay NULL.
UPDATE patients p
SET managing_provider_id = fp.id
FROM facility_providers fp
WHERE p.managing_provider_id IS NULL
  AND p.confirmed_by IS NOT NULL
  AND fp.user_id = p.confirmed_by;
