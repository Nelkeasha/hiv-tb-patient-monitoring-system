-- V32: Structured TB symptom & HIV risk screening on CHW provisional registration
--
-- Replaces free-text symptom/suspicion entry at screening time with standardized
-- boolean questions, per Rwanda Biomedical Centre protocol:
--   * TB: the RBC 4-symptom screen (+ chest pain) → presumptive-TB flag
--   * HIV: community HIV-testing eligibility risk questions → testing-referral flag
-- The free-text screening_notes column is kept unchanged as an optional addition.

ALTER TABLE patients
    -- RBC TB symptom screen
    ADD COLUMN tb_symptom_cough          BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN tb_symptom_fever          BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN tb_symptom_night_sweats   BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN tb_symptom_weight_loss    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN tb_symptom_chest_pain     BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN presumptive_tb            BOOLEAN NOT NULL DEFAULT FALSE,
    -- Community HIV testing-eligibility risk screen (highly sensitive)
    ADD COLUMN hiv_risk_never_tested       BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN hiv_risk_partner_positive   BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN hiv_risk_unprotected_sex    BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN hiv_risk_sti_treatment      BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN hiv_risk_recurrent_illness  BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN hiv_testing_referral        BOOLEAN NOT NULL DEFAULT FALSE,
    -- Optional CHW justification when referring for HIV testing despite a low-risk screen
    ADD COLUMN manual_referral_reason      VARCHAR(200);

-- Surveillance query support: presumptive-TB cases from community screening
CREATE INDEX idx_patients_presumptive_tb
    ON patients (presumptive_tb)
    WHERE presumptive_tb = TRUE;
