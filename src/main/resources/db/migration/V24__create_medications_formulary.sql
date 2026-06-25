-- V24: Normalize treatment_plans.medication_name (free text) into a
-- controlled medications_formulary table referenced by medication_id.

CREATE TABLE medications_formulary (
    id          UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name        VARCHAR(150) NOT NULL UNIQUE,
    dosage_form VARCHAR(50),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

INSERT INTO medications_formulary (name, dosage_form) VALUES
    ('Tenofovir/Lamivudine/Dolutegravir (TLD)',                       'Tablet'),
    ('Abacavir/Lamivudine (ABC/3TC)',                                 'Tablet'),
    ('Zidovudine/Lamivudine/Efavirenz (AZT/3TC/EFV)',                 'Tablet'),
    ('Lopinavir/Ritonavir (LPV/r)',                                   'Tablet'),
    ('Dolutegravir (DTG)',                                            'Tablet'),
    ('Isoniazid (INH)',                                               'Tablet'),
    ('Rifampicin/Isoniazid/Pyrazinamide/Ethambutol (RHZE)',           'Tablet'),
    ('Rifampicin/Isoniazid (RH)',                                     'Tablet'),
    ('Cotrimoxazole (CTX) Prophylaxis',                               'Tablet');

ALTER TABLE treatment_plans ADD COLUMN medication_id UUID;

-- Preserve any existing free-text values that don't match a seeded entry
-- by creating a formulary row for them, so no historical plan loses data.
INSERT INTO medications_formulary (name)
SELECT DISTINCT tp.medication_name
FROM treatment_plans tp
WHERE NOT EXISTS (
    SELECT 1 FROM medications_formulary mf WHERE mf.name = tp.medication_name
);

UPDATE treatment_plans tp
SET medication_id = mf.id
FROM medications_formulary mf
WHERE mf.name = tp.medication_name;

ALTER TABLE treatment_plans
    ALTER COLUMN medication_id SET NOT NULL,
    ADD CONSTRAINT fk_treatment_plans_medication
        FOREIGN KEY (medication_id) REFERENCES medications_formulary(id),
    DROP COLUMN medication_name;
