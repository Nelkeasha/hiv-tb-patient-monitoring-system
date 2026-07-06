-- V31: Structured symptom & side-effect screening on home visits (Gap B)
--
-- Replaces vague free-text symptom entry with standardized boolean flags so the
-- data becomes queryable for public-health surveillance — e.g. counting
-- presumptive-TB cases for active case finding, or tracking TLD drug toxicity
-- (peripheral neuropathy, hepatotoxicity) across a facility or region.
--
-- The existing free-text symptoms_reported / side_effects_reported columns are
-- KEPT as an optional "other / notes" escape hatch and remain the text source
-- for the FHIR Observation resources, so nothing downstream breaks.

ALTER TABLE home_visits
    -- WHO four-symptom TB screen (+ hemoptysis) — drives presumptive-TB case finding
    ADD COLUMN symptom_cough_ge2w      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN symptom_fever           BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN symptom_night_sweats    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN symptom_weight_loss     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN symptom_hemoptysis      BOOLEAN NOT NULL DEFAULT FALSE,
    -- Standardized ART / TLD side-effect checklist
    ADD COLUMN side_effect_neuropathy  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN side_effect_jaundice    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN side_effect_nausea      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN side_effect_rash        BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN side_effect_dizziness   BOOLEAN NOT NULL DEFAULT FALSE,
    -- Server-derived: TRUE when any WHO cardinal TB symptom is present
    ADD COLUMN presumptive_tb          BOOLEAN NOT NULL DEFAULT FALSE;

-- Partial index for the surveillance query "presumptive-TB flags in a period"
CREATE INDEX idx_home_visits_presumptive_tb
    ON home_visits (presumptive_tb)
    WHERE presumptive_tb = TRUE;
