-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ENUM Types
CREATE TYPE user_role AS ENUM (
    'PATIENT',
    'CHW',
    'FACILITY_PROVIDER',
    'SUPERVISOR',
    'SYSTEM_ADMIN'
);

CREATE TYPE diagnosis_type AS ENUM (
    'HIV',
    'TB',
    'HIV_TB_COINFECTION'
);

CREATE TYPE sync_status AS ENUM (
    'PENDING',
    'SYNCED',
    'FAILED'
);

CREATE TYPE alert_type AS ENUM (
    'MISSED_DOSE',
    'LOW_ADHERENCE',
    'FALSE_CONFIRMATION',
    'LOW_STOCK',
    'CLINICAL_DISCREPANCY',
    'EARLY_WARNING',
    'MISSED_VISIT'
);

CREATE TYPE alert_severity AS ENUM (
    'INFO',
    'WARNING',
    'CRITICAL'
);

CREATE TYPE risk_level AS ENUM (
    'LOW',
    'MODERATE',
    'HIGH',
    'CRITICAL'
);

CREATE TYPE confirmation_channel AS ENUM (
    'APP',
    'SMS'
);

CREATE TYPE confirmation_status AS ENUM (
    'CONFIRMED',
    'MISSED',
    'OUTSIDE_WINDOW',
    'SUSPICIOUS'
);

-- SYSTEM USERS TABLE
CREATE TABLE system_users (
                              id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              full_name VARCHAR(100) NOT NULL,
                              email VARCHAR(100) UNIQUE NOT NULL,
                              phone_number VARCHAR(20) UNIQUE NOT NULL,
                              password_hash VARCHAR(255) NOT NULL,
                              role user_role NOT NULL,
                              is_active BOOLEAN DEFAULT TRUE,
                              preferred_language VARCHAR(10) DEFAULT 'rw',
                              created_at TIMESTAMP DEFAULT NOW(),
                              updated_at TIMESTAMP DEFAULT NOW()
);

-- FACILITIES TABLE
CREATE TABLE facilities (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            name VARCHAR(100) NOT NULL,
                            location VARCHAR(200) NOT NULL,
                            district VARCHAR(100) NOT NULL,
                            fhir_endpoint_url VARCHAR(255),
                            is_active BOOLEAN DEFAULT TRUE,
                            created_at TIMESTAMP DEFAULT NOW()
);

-- CHWS TABLE
CREATE TABLE chws (
                      id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                      user_id UUID NOT NULL REFERENCES system_users(id),
                      facility_id UUID NOT NULL REFERENCES facilities(id),
                      assigned_village VARCHAR(100) NOT NULL,
                      assigned_sector VARCHAR(100) NOT NULL,
                      employee_code VARCHAR(50) UNIQUE NOT NULL,
                      is_active BOOLEAN DEFAULT TRUE,
                      created_at TIMESTAMP DEFAULT NOW()
);

-- FACILITY PROVIDERS TABLE
CREATE TABLE facility_providers (
                                    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                    user_id UUID NOT NULL REFERENCES system_users(id),
                                    facility_id UUID NOT NULL REFERENCES facilities(id),
                                    specialization VARCHAR(100),
                                    license_number VARCHAR(50),
                                    created_at TIMESTAMP DEFAULT NOW()
);

-- SUPERVISORS TABLE
CREATE TABLE supervisors (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             user_id UUID NOT NULL REFERENCES system_users(id),
                             facility_id UUID NOT NULL REFERENCES facilities(id),
                             district VARCHAR(100) NOT NULL,
                             created_at TIMESTAMP DEFAULT NOW()
);

-- PATIENTS TABLE
CREATE TABLE patients (
                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                          patient_code VARCHAR(20) UNIQUE NOT NULL,
                          full_name VARCHAR(100) NOT NULL,
                          date_of_birth DATE NOT NULL,
                          sex VARCHAR(10) NOT NULL,
                          national_id VARCHAR(16) UNIQUE,
                          phone_number VARCHAR(20),
                          has_smartphone BOOLEAN DEFAULT FALSE,
                          diagnosis_type diagnosis_type NOT NULL,
                          art_start_date DATE,
                          tb_treatment_start_date DATE,
                          household_location VARCHAR(255),
                          village VARCHAR(100),
                          sector VARCHAR(100),
                          district VARCHAR(100),
                          chw_id UUID NOT NULL REFERENCES chws(id),
                          facility_id UUID NOT NULL REFERENCES facilities(id),
                          fhir_patient_id VARCHAR(100) UNIQUE,
                          sync_status sync_status DEFAULT 'PENDING',
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT NOW(),
                          updated_at TIMESTAMP DEFAULT NOW()
);

-- HOME VISITS TABLE
CREATE TABLE home_visits (
                             id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             patient_id UUID NOT NULL REFERENCES patients(id),
                             chw_id UUID NOT NULL REFERENCES chws(id),
                             visit_date TIMESTAMP NOT NULL,
                             adherence_status VARCHAR(20) NOT NULL,
                             pill_count_recorded INTEGER,
                             pill_count_expected INTEGER,
                             pill_count_discrepancy BOOLEAN DEFAULT FALSE,
                             symptoms_reported TEXT,
                             side_effects_reported TEXT,
                             psychosocial_notes TEXT,
                             next_visit_date TIMESTAMP,
                             fhir_observation_id VARCHAR(100),
                             sync_status sync_status DEFAULT 'PENDING',
                             created_at TIMESTAMP DEFAULT NOW()
);

-- MEDICATION RECORDS TABLE
CREATE TABLE medication_records (
                                    id BIGSERIAL PRIMARY KEY,
                                    patient_id UUID NOT NULL REFERENCES patients(id),
                                    chw_id UUID NOT NULL REFERENCES chws(id),
                                    medication_name VARCHAR(100) NOT NULL,
                                    period_start DATE NOT NULL,
                                    period_end DATE NOT NULL,
                                    doses_scheduled INTEGER NOT NULL,
                                    doses_taken INTEGER NOT NULL,
                                    adherence_pct DECIMAL(5,2) NOT NULL,
                                    below_threshold BOOLEAN DEFAULT FALSE,
                                    fhir_resource_id VARCHAR(100),
                                    sync_status sync_status DEFAULT 'PENDING',
                                    calculated_at TIMESTAMP DEFAULT NOW()
);

-- CONFIRMATION LOGS TABLE
CREATE TABLE confirmation_logs (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   patient_id UUID NOT NULL REFERENCES patients(id),
                                   scheduled_dose_time TIMESTAMP NOT NULL,
                                   confirmation_time TIMESTAMP,
                                   channel confirmation_channel NOT NULL,
                                   status confirmation_status NOT NULL,
                                   response_time_seconds INTEGER,
                                   window_open_time TIMESTAMP NOT NULL,
                                   window_close_time TIMESTAMP NOT NULL,
                                   is_within_window BOOLEAN DEFAULT FALSE,
                                   created_at TIMESTAMP DEFAULT NOW()
);

-- STOCK RECORDS TABLE
CREATE TABLE stock_records (
                               id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                               chw_id UUID NOT NULL REFERENCES chws(id),
                               medication_name VARCHAR(100) NOT NULL,
                               current_quantity INTEGER NOT NULL DEFAULT 0,
                               reorder_level INTEGER NOT NULL DEFAULT 14,
                               unit VARCHAR(20) DEFAULT 'tablets',
                               last_restocked_at TIMESTAMP,
                               days_remaining INTEGER,
                               resupply_requested BOOLEAN DEFAULT FALSE,
                               created_at TIMESTAMP DEFAULT NOW(),
                               updated_at TIMESTAMP DEFAULT NOW()
);

-- DISPENSING EVENTS TABLE
CREATE TABLE dispensing_events (
                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                   stock_id UUID NOT NULL REFERENCES stock_records(id),
                                   patient_id UUID NOT NULL REFERENCES patients(id),
                                   chw_id UUID NOT NULL REFERENCES chws(id),
                                   medication_name VARCHAR(100) NOT NULL,
                                   quantity_dispensed INTEGER NOT NULL,
                                   dispensed_at TIMESTAMP DEFAULT NOW(),
                                   visit_id UUID REFERENCES home_visits(id),
                                   sync_status sync_status DEFAULT 'PENDING'
);

-- ALERTS TABLE
CREATE TABLE alerts (
                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                        patient_id UUID REFERENCES patients(id),
                        chw_id UUID REFERENCES chws(id),
                        provider_id UUID REFERENCES facility_providers(id),
                        supervisor_id UUID REFERENCES supervisors(id),
                        alert_type alert_type NOT NULL,
                        severity alert_severity NOT NULL,
                        title VARCHAR(200) NOT NULL,
                        message TEXT NOT NULL,
                        is_read BOOLEAN DEFAULT FALSE,
                        is_resolved BOOLEAN DEFAULT FALSE,
                        resolved_at TIMESTAMP,
                        created_at TIMESTAMP DEFAULT NOW()
);

-- AI RISK SCORES TABLE
CREATE TABLE ai_risk_scores (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                patient_id UUID NOT NULL REFERENCES patients(id),
                                risk_level risk_level NOT NULL,
                                risk_score DECIMAL(5,2) NOT NULL,
                                suspicion_score INTEGER DEFAULT 0,
                                missed_doses_7d INTEGER DEFAULT 0,
                                missed_doses_14d INTEGER DEFAULT 0,
                                missed_doses_30d INTEGER DEFAULT 0,
                                avg_response_time_seconds INTEGER,
                                side_effect_reports_14d INTEGER DEFAULT 0,
                                missed_visits_30d INTEGER DEFAULT 0,
                                timestamp_anomaly_detected BOOLEAN DEFAULT FALSE,
                                pill_count_discrepancy_detected BOOLEAN DEFAULT FALSE,
                                window_violation_detected BOOLEAN DEFAULT FALSE,
                                recommended_action TEXT,
                                calculated_at TIMESTAMP DEFAULT NOW()
);

-- TREATMENT PLANS TABLE
CREATE TABLE treatment_plans (
                                 id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                 patient_id UUID NOT NULL REFERENCES patients(id),
                                 provider_id UUID REFERENCES facility_providers(id),
                                 regimen VARCHAR(200) NOT NULL,
                                 start_date DATE NOT NULL,
                                 end_date DATE,
                                 is_active BOOLEAN DEFAULT TRUE,
                                 fhir_care_plan_id VARCHAR(100),
                                 notes TEXT,
                                 created_at TIMESTAMP DEFAULT NOW(),
                                 updated_at TIMESTAMP DEFAULT NOW()
);

-- FHIR SYNC LOGS TABLE
CREATE TABLE fhir_sync_logs (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                chw_id UUID REFERENCES chws(id),
                                sync_started_at TIMESTAMP NOT NULL,
                                sync_completed_at TIMESTAMP,
                                records_synced INTEGER DEFAULT 0,
                                records_failed INTEGER DEFAULT 0,
                                sync_status VARCHAR(20) NOT NULL,
                                error_log TEXT,
                                created_at TIMESTAMP DEFAULT NOW()
);

-- AUDIT LOGS TABLE
CREATE TABLE audit_logs (
                            id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                            user_id UUID REFERENCES system_users(id),
                            action VARCHAR(100) NOT NULL,
                            target_table VARCHAR(50),
                            target_id UUID,
                            ip_address VARCHAR(45),
                            details JSONB,
                            created_at TIMESTAMP DEFAULT NOW()
);

-- REFRESH TOKENS TABLE
CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                                user_id UUID NOT NULL REFERENCES system_users(id),
                                token VARCHAR(255) UNIQUE NOT NULL,
                                expires_at TIMESTAMP NOT NULL,
                                is_revoked BOOLEAN DEFAULT FALSE,
                                created_at TIMESTAMP DEFAULT NOW()
);

-- INDEXES FOR PERFORMANCE
CREATE INDEX idx_patients_chw_id ON patients(chw_id);
CREATE INDEX idx_patients_facility_id ON patients(facility_id);
CREATE INDEX idx_home_visits_patient_id ON home_visits(patient_id);
CREATE INDEX idx_home_visits_chw_id ON home_visits(chw_id);
CREATE INDEX idx_confirmation_logs_patient_id ON confirmation_logs(patient_id);
CREATE INDEX idx_alerts_patient_id ON alerts(patient_id);
CREATE INDEX idx_alerts_chw_id ON alerts(chw_id);
CREATE INDEX idx_ai_risk_scores_patient_id ON ai_risk_scores(patient_id);
CREATE INDEX idx_stock_records_chw_id ON stock_records(chw_id);
CREATE INDEX idx_dispensing_events_patient_id ON dispensing_events(patient_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_medication_records_patient_id ON medication_records(patient_id);