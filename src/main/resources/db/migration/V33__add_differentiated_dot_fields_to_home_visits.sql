-- V33: Differentiated DOT model on home visits (diagnosis-adaptive Card A / Card B)
--
-- Home visits are for already-enrolled, CONFIRMED patients on treatment. The form
-- shows ART-monitoring fields (Card A) for HIV, and Directly-Observed-Therapy
-- fields (Card B) for TB, based on the patient's known diagnosisType.
--
-- Regulatory note: dot_observed and all TB fields record the CHW *observing and
-- documenting* the patient taking their own already-dispensed medication. The CHW
-- never dispenses or hands over medication — no stock/dispensing field exists here.

ALTER TABLE home_visits
    -- Card B — Directly Observed Therapy (TB / HIV_TB_COINFECTION only)
    ADD COLUMN dot_observed          BOOLEAN,                 -- CHW watched the patient swallow today's dose
    ADD COLUMN tb_side_effects       JSONB,                   -- { jaundice, vomiting, jointPain, visionChanges, rash }
    ADD COLUMN home_ventilation_ok   BOOLEAN,                 -- infection-control: adequate home ventilation
    ADD COLUMN cough_hygiene_ok      BOOLEAN,                 -- infection-control: patient practices cough hygiene
    ADD COLUMN next_dot_date         DATE,                    -- next scheduled in-person DOT review
    -- Card A — HIV / ART monitoring (HIV / HIV_TB_COINFECTION only)
    ADD COLUMN art_side_effects      JSONB,                   -- { jaundice, neuropathy, vomiting, rash }
    -- Part 3 — why this in-person visit was generated (nullable; set by the trigger engine)
    ADD COLUMN home_visit_trigger    VARCHAR(30);             -- MISSED_DOSES | SIDE_EFFECT | IIT_ESCALATED | HIGH_RISK | PERIODIC_REVIEW | INITIAL_ASSESSMENT
