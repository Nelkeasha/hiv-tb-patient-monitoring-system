--
-- PostgreSQL database dump
--

\restrict aXsSEDsjbPuPcREd9Zm7lWoYKA4TZoSlQ2XejX1gHvIq8vYw51ryrdxa25KWR16

-- Dumped from database version 16.14 (Debian 16.14-1.pgdg12+1)
-- Dumped by pg_dump version 16.14 (Debian 16.14-1.pgdg13+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

ALTER TABLE IF EXISTS ONLY public.treatment_plans DROP CONSTRAINT IF EXISTS treatment_plans_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.treatment_plans DROP CONSTRAINT IF EXISTS treatment_plans_created_by_fkey;
ALTER TABLE IF EXISTS ONLY public.tracing_tasks DROP CONSTRAINT IF EXISTS tracing_tasks_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.tracing_tasks DROP CONSTRAINT IF EXISTS tracing_tasks_escalated_to_fkey;
ALTER TABLE IF EXISTS ONLY public.tracing_tasks DROP CONSTRAINT IF EXISTS tracing_tasks_chw_id_fkey;
ALTER TABLE IF EXISTS ONLY public.supervisors DROP CONSTRAINT IF EXISTS supervisors_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.supervisors DROP CONSTRAINT IF EXISTS supervisors_facility_id_fkey;
ALTER TABLE IF EXISTS ONLY public.refresh_tokens DROP CONSTRAINT IF EXISTS refresh_tokens_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.referrals DROP CONSTRAINT IF EXISTS referrals_referred_by_chw_id_fkey;
ALTER TABLE IF EXISTS ONLY public.referrals DROP CONSTRAINT IF EXISTS referrals_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.referrals DROP CONSTRAINT IF EXISTS referrals_confirmed_by_provider_id_fkey;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_screened_by_chw_id_fkey;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_facility_id_fkey;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_confirmed_by_fkey;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_chw_id_fkey;
ALTER TABLE IF EXISTS ONLY public.medication_records DROP CONSTRAINT IF EXISTS medication_records_plan_id_fkey;
ALTER TABLE IF EXISTS ONLY public.medication_records DROP CONSTRAINT IF EXISTS medication_records_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.locations DROP CONSTRAINT IF EXISTS locations_parent_id_fkey;
ALTER TABLE IF EXISTS ONLY public.lab_results DROP CONSTRAINT IF EXISTS lab_results_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.home_visits DROP CONSTRAINT IF EXISTS home_visits_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.home_visits DROP CONSTRAINT IF EXISTS home_visits_chw_id_fkey;
ALTER TABLE IF EXISTS ONLY public.fhir_sync_logs DROP CONSTRAINT IF EXISTS fhir_sync_logs_chw_id_fkey;
ALTER TABLE IF EXISTS ONLY public.facility_providers DROP CONSTRAINT IF EXISTS facility_providers_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.facility_providers DROP CONSTRAINT IF EXISTS facility_providers_facility_id_fkey;
ALTER TABLE IF EXISTS ONLY public.dose_schedules DROP CONSTRAINT IF EXISTS dose_schedules_plan_id_fkey;
ALTER TABLE IF EXISTS ONLY public.dose_schedules DROP CONSTRAINT IF EXISTS dose_schedules_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.dose_schedules DROP CONSTRAINT IF EXISTS dose_schedules_created_by_fkey;
ALTER TABLE IF EXISTS ONLY public.confirmation_logs DROP CONSTRAINT IF EXISTS confirmation_logs_schedule_id_fkey;
ALTER TABLE IF EXISTS ONLY public.confirmation_logs DROP CONSTRAINT IF EXISTS confirmation_logs_plan_id_fkey;
ALTER TABLE IF EXISTS ONLY public.confirmation_logs DROP CONSTRAINT IF EXISTS confirmation_logs_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.chws DROP CONSTRAINT IF EXISTS chws_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.chws DROP CONSTRAINT IF EXISTS chws_facility_id_fkey;
ALTER TABLE IF EXISTS ONLY public.audit_logs DROP CONSTRAINT IF EXISTS audit_logs_user_id_fkey;
ALTER TABLE IF EXISTS ONLY public.alerts DROP CONSTRAINT IF EXISTS alerts_supervisor_id_fkey;
ALTER TABLE IF EXISTS ONLY public.alerts DROP CONSTRAINT IF EXISTS alerts_resolved_by_fkey;
ALTER TABLE IF EXISTS ONLY public.alerts DROP CONSTRAINT IF EXISTS alerts_provider_id_fkey;
ALTER TABLE IF EXISTS ONLY public.alerts DROP CONSTRAINT IF EXISTS alerts_patient_id_fkey;
ALTER TABLE IF EXISTS ONLY public.alerts DROP CONSTRAINT IF EXISTS alerts_chw_id_fkey;
ALTER TABLE IF EXISTS ONLY public.ai_risk_scores DROP CONSTRAINT IF EXISTS ai_risk_scores_patient_id_fkey;
DROP INDEX IF EXISTS public.idx_tracing_tasks_status;
DROP INDEX IF EXISTS public.idx_tracing_tasks_patient_id;
DROP INDEX IF EXISTS public.idx_tracing_tasks_patient_date_open;
DROP INDEX IF EXISTS public.idx_tracing_tasks_created_at;
DROP INDEX IF EXISTS public.idx_tracing_tasks_chw_id;
DROP INDEX IF EXISTS public.idx_system_users_fcm_token;
DROP INDEX IF EXISTS public.idx_referrals_status;
DROP INDEX IF EXISTS public.idx_referrals_patient_id;
DROP INDEX IF EXISTS public.idx_referrals_chw_id;
DROP INDEX IF EXISTS public.idx_patients_user_id;
DROP INDEX IF EXISTS public.idx_patients_registration_status;
DROP INDEX IF EXISTS public.idx_patients_referral_id;
DROP INDEX IF EXISTS public.idx_patients_facility_id;
DROP INDEX IF EXISTS public.idx_patients_chw_id;
DROP INDEX IF EXISTS public.idx_patients_chw_assignment_status;
DROP INDEX IF EXISTS public.idx_medication_records_patient_id;
DROP INDEX IF EXISTS public.idx_locations_parent_id;
DROP INDEX IF EXISTS public.idx_locations_location_type;
DROP INDEX IF EXISTS public.idx_lab_results_patient_loinc_observed_at;
DROP INDEX IF EXISTS public.idx_lab_results_patient_loinc_date;
DROP INDEX IF EXISTS public.idx_home_visits_patient_id;
DROP INDEX IF EXISTS public.idx_home_visits_client_request_id;
DROP INDEX IF EXISTS public.idx_home_visits_chw_id;
DROP INDEX IF EXISTS public.idx_dose_schedules_plan_id;
DROP INDEX IF EXISTS public.idx_dose_schedules_patient_id;
DROP INDEX IF EXISTS public.idx_confirmation_logs_patient_id;
DROP INDEX IF EXISTS public.idx_audit_logs_user_id;
DROP INDEX IF EXISTS public.idx_alerts_patient_id;
DROP INDEX IF EXISTS public.idx_alerts_chw_id;
DROP INDEX IF EXISTS public.idx_ai_risk_scores_patient_id;
DROP INDEX IF EXISTS public.flyway_schema_history_s_idx;
ALTER TABLE IF EXISTS ONLY public.treatment_plans DROP CONSTRAINT IF EXISTS treatment_plans_pkey;
ALTER TABLE IF EXISTS ONLY public.tracing_tasks DROP CONSTRAINT IF EXISTS tracing_tasks_pkey;
ALTER TABLE IF EXISTS ONLY public.system_users DROP CONSTRAINT IF EXISTS system_users_pkey;
ALTER TABLE IF EXISTS ONLY public.system_users DROP CONSTRAINT IF EXISTS system_users_phone_number_key;
ALTER TABLE IF EXISTS ONLY public.system_users DROP CONSTRAINT IF EXISTS system_users_email_key;
ALTER TABLE IF EXISTS ONLY public.system_settings DROP CONSTRAINT IF EXISTS system_settings_pkey;
ALTER TABLE IF EXISTS ONLY public.supervisors DROP CONSTRAINT IF EXISTS supervisors_pkey;
ALTER TABLE IF EXISTS ONLY public.refresh_tokens DROP CONSTRAINT IF EXISTS refresh_tokens_token_key;
ALTER TABLE IF EXISTS ONLY public.refresh_tokens DROP CONSTRAINT IF EXISTS refresh_tokens_pkey;
ALTER TABLE IF EXISTS ONLY public.referrals DROP CONSTRAINT IF EXISTS referrals_pkey;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_referral_id_key;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_pkey;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_patient_code_key;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_national_id_key;
ALTER TABLE IF EXISTS ONLY public.patients DROP CONSTRAINT IF EXISTS patients_fhir_patient_id_key;
ALTER TABLE IF EXISTS ONLY public.medication_records DROP CONSTRAINT IF EXISTS medication_records_pkey;
ALTER TABLE IF EXISTS ONLY public.locations DROP CONSTRAINT IF EXISTS locations_pkey;
ALTER TABLE IF EXISTS ONLY public.locations DROP CONSTRAINT IF EXISTS locations_code_key;
ALTER TABLE IF EXISTS ONLY public.lab_results DROP CONSTRAINT IF EXISTS lab_results_pkey;
ALTER TABLE IF EXISTS ONLY public.home_visits DROP CONSTRAINT IF EXISTS home_visits_pkey;
ALTER TABLE IF EXISTS ONLY public.flyway_schema_history DROP CONSTRAINT IF EXISTS flyway_schema_history_pk;
ALTER TABLE IF EXISTS ONLY public.fhir_sync_logs DROP CONSTRAINT IF EXISTS fhir_sync_logs_pkey;
ALTER TABLE IF EXISTS ONLY public.facility_providers DROP CONSTRAINT IF EXISTS facility_providers_pkey;
ALTER TABLE IF EXISTS ONLY public.facilities DROP CONSTRAINT IF EXISTS facilities_pkey;
ALTER TABLE IF EXISTS ONLY public.dose_schedules DROP CONSTRAINT IF EXISTS dose_schedules_pkey;
ALTER TABLE IF EXISTS ONLY public.confirmation_logs DROP CONSTRAINT IF EXISTS confirmation_logs_pkey;
ALTER TABLE IF EXISTS ONLY public.chws DROP CONSTRAINT IF EXISTS chws_pkey;
ALTER TABLE IF EXISTS ONLY public.chws DROP CONSTRAINT IF EXISTS chws_employee_code_key;
ALTER TABLE IF EXISTS ONLY public.audit_logs DROP CONSTRAINT IF EXISTS audit_logs_pkey;
ALTER TABLE IF EXISTS ONLY public.alerts DROP CONSTRAINT IF EXISTS alerts_pkey;
ALTER TABLE IF EXISTS ONLY public.ai_risk_scores DROP CONSTRAINT IF EXISTS ai_risk_scores_pkey;
ALTER TABLE IF EXISTS public.medication_records ALTER COLUMN id DROP DEFAULT;
ALTER TABLE IF EXISTS public.locations ALTER COLUMN id DROP DEFAULT;
DROP TABLE IF EXISTS public.treatment_plans;
DROP TABLE IF EXISTS public.tracing_tasks;
DROP TABLE IF EXISTS public.system_users;
DROP TABLE IF EXISTS public.system_settings;
DROP TABLE IF EXISTS public.supervisors;
DROP TABLE IF EXISTS public.refresh_tokens;
DROP TABLE IF EXISTS public.referrals;
DROP TABLE IF EXISTS public.patients;
DROP SEQUENCE IF EXISTS public.medication_records_id_seq;
DROP TABLE IF EXISTS public.medication_records;
DROP SEQUENCE IF EXISTS public.locations_id_seq;
DROP TABLE IF EXISTS public.locations;
DROP TABLE IF EXISTS public.lab_results;
DROP TABLE IF EXISTS public.home_visits;
DROP TABLE IF EXISTS public.flyway_schema_history;
DROP TABLE IF EXISTS public.fhir_sync_logs;
DROP TABLE IF EXISTS public.facility_providers;
DROP TABLE IF EXISTS public.facilities;
DROP TABLE IF EXISTS public.dose_schedules;
DROP TABLE IF EXISTS public.confirmation_logs;
DROP TABLE IF EXISTS public.chws;
DROP TABLE IF EXISTS public.audit_logs;
DROP TABLE IF EXISTS public.alerts;
DROP TABLE IF EXISTS public.ai_risk_scores;
DROP TYPE IF EXISTS public.user_role;
DROP TYPE IF EXISTS public.sync_status;
DROP TYPE IF EXISTS public.risk_level;
DROP TYPE IF EXISTS public.referral_urgency;
DROP TYPE IF EXISTS public.referral_status;
DROP TYPE IF EXISTS public.diagnosis_type;
DROP TYPE IF EXISTS public.confirmation_channel;
DROP TYPE IF EXISTS public.alert_type;
DROP TYPE IF EXISTS public.alert_severity;
DROP EXTENSION IF EXISTS "uuid-ossp";
-- *not* dropping schema, since initdb creates it
--
-- Name: public; Type: SCHEMA; Schema: -; Owner: -
--

-- *not* creating schema, since initdb creates it


--
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- Name: alert_severity; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.alert_severity AS ENUM (
    'INFO',
    'WARNING',
    'CRITICAL'
);


--
-- Name: alert_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.alert_type AS ENUM (
    'MISSED_DOSE',
    'FALSE_CONFIRMATION',
    'CLINICAL_DISCREPANCY',
    'EARLY_WARNING',
    'LTFU_TRACING',
    'LTFU_CONFIRMED',
    'LTFU_TRACING_RESOLVED',
    'SYNC_FAILURE',
    'NEW_PATIENT_ASSIGNMENT'
);


--
-- Name: confirmation_channel; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.confirmation_channel AS ENUM (
    'APP',
    'SMS'
);


--
-- Name: diagnosis_type; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.diagnosis_type AS ENUM (
    'HIV',
    'TB',
    'HIV_TB_COINFECTION'
);


--
-- Name: referral_status; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.referral_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'MODIFIED',
    'ATTENDED',
    'NOT_ATTENDED',
    'CANCELLED'
);


--
-- Name: referral_urgency; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.referral_urgency AS ENUM (
    'ROUTINE',
    'URGENT',
    'EMERGENCY'
);


--
-- Name: risk_level; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.risk_level AS ENUM (
    'LOW',
    'MODERATE',
    'HIGH',
    'CRITICAL'
);


--
-- Name: sync_status; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.sync_status AS ENUM (
    'PENDING',
    'SYNCED',
    'FAILED'
);


--
-- Name: user_role; Type: TYPE; Schema: public; Owner: -
--

CREATE TYPE public.user_role AS ENUM (
    'PATIENT',
    'CHW',
    'FACILITY_PROVIDER',
    'SUPERVISOR',
    'SYSTEM_ADMIN',
    'CLINICAL_STAFF',
    'ADMIN'
);


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: ai_risk_scores; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.ai_risk_scores (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid NOT NULL,
    risk_level public.risk_level NOT NULL,
    risk_score numeric(5,2) NOT NULL,
    suspicion_score integer DEFAULT 0,
    missed_doses_7d integer DEFAULT 0,
    missed_doses_14d integer DEFAULT 0,
    missed_doses_30d integer DEFAULT 0,
    avg_response_time_seconds integer,
    side_effect_reports_14d integer DEFAULT 0,
    missed_visits_30d integer DEFAULT 0,
    timestamp_anomaly_detected boolean DEFAULT false,
    pill_count_discrepancy_detected boolean DEFAULT false,
    window_violation_detected boolean DEFAULT false,
    recommended_action text,
    calculated_at timestamp without time zone DEFAULT now()
);


--
-- Name: alerts; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.alerts (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid,
    chw_id uuid,
    provider_id uuid,
    supervisor_id uuid,
    alert_type public.alert_type NOT NULL,
    severity public.alert_severity NOT NULL,
    title character varying(200) NOT NULL,
    message text NOT NULL,
    is_read boolean DEFAULT false,
    is_resolved boolean DEFAULT false,
    resolved_at timestamp without time zone,
    created_at timestamp without time zone DEFAULT now(),
    resolved_by uuid,
    escalated_at timestamp without time zone
);


--
-- Name: audit_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.audit_logs (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid,
    action character varying(100) NOT NULL,
    target_table character varying(50),
    target_id uuid,
    ip_address character varying(45),
    details jsonb,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: chws; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.chws (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    facility_id uuid NOT NULL,
    assigned_village character varying(100) NOT NULL,
    assigned_sector character varying(100) NOT NULL,
    employee_code character varying(50) NOT NULL,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: confirmation_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.confirmation_logs (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid NOT NULL,
    confirmation_method public.confirmation_channel NOT NULL,
    response_time_seconds integer,
    window_open_time timestamp without time zone NOT NULL,
    window_close_time timestamp without time zone NOT NULL,
    is_within_window boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT now(),
    plan_id uuid,
    schedule_id uuid,
    scheduled_date date NOT NULL,
    confirmed_at timestamp without time zone,
    raw_sms_response character varying(20),
    is_missed boolean DEFAULT false NOT NULL,
    ai_suspicion_flag boolean DEFAULT false NOT NULL,
    suspicion_reason character varying(500)
);


--
-- Name: dose_schedules; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.dose_schedules (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    plan_id uuid NOT NULL,
    patient_id uuid NOT NULL,
    dose_time time without time zone NOT NULL,
    dose_label character varying(50),
    notification_method public.confirmation_channel DEFAULT 'APP'::public.confirmation_channel,
    window_duration_minutes integer DEFAULT 45,
    is_active boolean DEFAULT true,
    created_by uuid,
    created_at timestamp without time zone DEFAULT now(),
    prescription_source text
);


--
-- Name: facilities; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.facilities (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(100) NOT NULL,
    location character varying(200) NOT NULL,
    district character varying(100) NOT NULL,
    fhir_endpoint_url character varying(255),
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: facility_providers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.facility_providers (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    facility_id uuid NOT NULL,
    specialization character varying(100),
    license_number character varying(50),
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: fhir_sync_logs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.fhir_sync_logs (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    chw_id uuid,
    sync_started_at timestamp without time zone NOT NULL,
    sync_completed_at timestamp without time zone,
    records_synced integer DEFAULT 0,
    records_failed integer DEFAULT 0,
    sync_status character varying(20) NOT NULL,
    error_log text,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


--
-- Name: home_visits; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.home_visits (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid NOT NULL,
    chw_id uuid NOT NULL,
    visit_date timestamp without time zone NOT NULL,
    adherence_status character varying(20) NOT NULL,
    pill_count_recorded integer,
    pill_count_expected integer,
    pill_count_discrepancy boolean DEFAULT false,
    symptoms_reported text,
    side_effects_reported text,
    psychosocial_notes text,
    next_visit_date timestamp without time zone,
    fhir_observation_id character varying(100),
    sync_status public.sync_status DEFAULT 'PENDING'::public.sync_status,
    created_at timestamp without time zone DEFAULT now(),
    visit_status character varying(20) DEFAULT 'ATTENDED_TO'::character varying NOT NULL,
    client_request_id uuid
);


--
-- Name: lab_results; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.lab_results (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid NOT NULL,
    loinc_code character varying(20) NOT NULL,
    value numeric(12,4) NOT NULL,
    unit character varying(30),
    observed_at timestamp without time zone NOT NULL,
    fhir_observation_id character varying(100),
    created_at timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: locations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.locations (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    code character varying(10) NOT NULL,
    description character varying(500),
    location_type character varying(20) NOT NULL,
    parent_id bigint,
    population bigint,
    area_km2 double precision,
    village_chief character varying(255),
    CONSTRAINT locations_location_type_check CHECK (((location_type)::text = ANY ((ARRAY['PROVINCE'::character varying, 'DISTRICT'::character varying, 'SECTOR'::character varying, 'CELL'::character varying, 'VILLAGE'::character varying])::text[])))
);


--
-- Name: locations_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.locations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: locations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.locations_id_seq OWNED BY public.locations.id;


--
-- Name: medication_records; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.medication_records (
    id bigint NOT NULL,
    patient_id uuid NOT NULL,
    period_start date NOT NULL,
    period_end date NOT NULL,
    doses_scheduled integer NOT NULL,
    adherence_pct numeric(5,2) NOT NULL,
    below_threshold boolean DEFAULT false,
    sync_status public.sync_status DEFAULT 'PENDING'::public.sync_status,
    plan_id uuid,
    doses_confirmed integer NOT NULL,
    doses_verified integer NOT NULL,
    false_confirmation_flag boolean DEFAULT false,
    fhir_statement_id character varying(100),
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: medication_records_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.medication_records_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: medication_records_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.medication_records_id_seq OWNED BY public.medication_records.id;


--
-- Name: patients; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.patients (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_code character varying(20) NOT NULL,
    full_name character varying(100) NOT NULL,
    date_of_birth date NOT NULL,
    sex character varying(10) NOT NULL,
    national_id character varying(16),
    phone_number character varying(20),
    has_smartphone boolean DEFAULT false,
    diagnosis_type public.diagnosis_type NOT NULL,
    art_start_date date,
    tb_treatment_start_date date,
    household_location character varying(255),
    village character varying(100),
    sector character varying(100),
    district character varying(100),
    chw_id uuid NOT NULL,
    facility_id uuid NOT NULL,
    fhir_patient_id character varying(100),
    sync_status public.sync_status DEFAULT 'PENDING'::public.sync_status,
    is_active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    user_id uuid,
    registration_route character varying(20) DEFAULT 'FACILITY'::character varying NOT NULL,
    registration_status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    referral_id character varying(30),
    screened_by_chw_id uuid,
    screened_at timestamp without time zone,
    confirmed_by uuid,
    confirmed_at timestamp without time zone,
    suspected_condition character varying(50),
    screening_symptoms text,
    screening_notes text,
    lab_result_notes text,
    province character varying(100),
    cell character varying(100),
    chw_assignment_status character varying(20) DEFAULT 'ACCEPTED'::character varying NOT NULL,
    chw_assigned_at timestamp without time zone,
    chw_accepted_at timestamp without time zone,
    chw_assignment_reminder_sent_at timestamp without time zone,
    chw_assignment_escalated_at timestamp without time zone
);


--
-- Name: referrals; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.referrals (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid NOT NULL,
    referred_by_chw_id uuid NOT NULL,
    confirmed_by_provider_id uuid,
    referral_date date NOT NULL,
    referral_reason text NOT NULL,
    urgency public.referral_urgency NOT NULL,
    status public.referral_status DEFAULT 'PENDING'::public.referral_status NOT NULL,
    facility_appointment_date date,
    provider_notes text,
    attendance_notes text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


--
-- Name: refresh_tokens; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.refresh_tokens (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    token character varying(255) NOT NULL,
    expires_at timestamp without time zone NOT NULL,
    is_revoked boolean DEFAULT false,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: supervisors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.supervisors (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    user_id uuid NOT NULL,
    facility_id uuid NOT NULL,
    district character varying(100) NOT NULL,
    created_at timestamp without time zone DEFAULT now()
);


--
-- Name: system_settings; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.system_settings (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    missed_dose_threshold integer DEFAULT 2 NOT NULL,
    low_stock_days integer DEFAULT 14 NOT NULL,
    confirm_window_minutes integer DEFAULT 45 NOT NULL,
    high_risk_threshold integer DEFAULT 70 NOT NULL,
    critical_risk_threshold integer DEFAULT 85 NOT NULL,
    updated_at timestamp without time zone DEFAULT now()
);


--
-- Name: system_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.system_users (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    full_name character varying(100) NOT NULL,
    email character varying(100) NOT NULL,
    phone_number character varying(20) NOT NULL,
    password_hash character varying(255) NOT NULL,
    role public.user_role NOT NULL,
    is_active boolean DEFAULT true,
    preferred_language character varying(10) DEFAULT 'rw'::character varying,
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    must_change_password boolean DEFAULT false NOT NULL,
    fcm_token character varying(255),
    failed_login_attempts integer DEFAULT 0,
    account_locked boolean DEFAULT false
);


--
-- Name: tracing_tasks; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tracing_tasks (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid NOT NULL,
    chw_id uuid NOT NULL,
    missed_appointment_date date NOT NULL,
    days_since_missed integer DEFAULT 0 NOT NULL,
    reason character varying(30) NOT NULL,
    status character varying(20) DEFAULT 'LATE'::character varying NOT NULL,
    ltfu_confirmed_at timestamp without time zone,
    outcome character varying(30),
    disengagement_reason character varying(30),
    resolution_plan text,
    proxy_authorized boolean DEFAULT false NOT NULL,
    proxy_name character varying(100),
    notes text,
    escalated_to uuid,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    resolved_at timestamp without time zone,
    CONSTRAINT tracing_tasks_disengagement_reason_check CHECK (((disengagement_reason IS NULL) OR ((disengagement_reason)::text = ANY ((ARRAY['STIGMA'::character varying, 'TRANSPORT_COST'::character varying, 'SIDE_EFFECTS'::character varying, 'FEELING_HEALTHY'::character varying, 'WORK_RELOCATION'::character varying, 'FAMILY_ISSUES'::character varying, 'OTHER'::character varying])::text[])))),
    CONSTRAINT tracing_tasks_outcome_check CHECK (((outcome IS NULL) OR ((outcome)::text = ANY ((ARRAY['PATIENT_FOUND'::character varying, 'PATIENT_REFUSED'::character varying, 'PATIENT_HOSPITALIZED'::character varying, 'PROXY_AUTHORIZED'::character varying, 'UNABLE_TO_LOCATE'::character varying])::text[])))),
    CONSTRAINT tracing_tasks_reason_check CHECK (((reason)::text = ANY ((ARRAY['MISSED_REFILL'::character varying, 'MISSED_APPOINTMENT'::character varying, 'LOST_TO_FOLLOWUP'::character varying])::text[]))),
    CONSTRAINT tracing_tasks_status_check CHECK (((status)::text = ANY ((ARRAY['LATE'::character varying, 'CHW_ASSIGNED'::character varying, 'RESOLVED'::character varying, 'LTFU_CONFIRMED'::character varying, 'ESCALATED'::character varying])::text[])))
);


--
-- Name: treatment_plans; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.treatment_plans (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    patient_id uuid NOT NULL,
    start_date date NOT NULL,
    end_date date,
    is_active boolean DEFAULT true,
    fhir_care_plan_id character varying(100),
    created_at timestamp without time zone DEFAULT now(),
    updated_at timestamp without time zone DEFAULT now(),
    medication_name character varying(100) NOT NULL,
    dosage character varying(50) NOT NULL,
    frequency character varying(50) NOT NULL,
    sync_status public.sync_status DEFAULT 'PENDING'::public.sync_status,
    created_by uuid
);


--
-- Name: locations id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.locations ALTER COLUMN id SET DEFAULT nextval('public.locations_id_seq'::regclass);


--
-- Name: medication_records id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.medication_records ALTER COLUMN id SET DEFAULT nextval('public.medication_records_id_seq'::regclass);


--
-- Data for Name: ai_risk_scores; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.ai_risk_scores (id, patient_id, risk_level, risk_score, suspicion_score, missed_doses_7d, missed_doses_14d, missed_doses_30d, avg_response_time_seconds, side_effect_reports_14d, missed_visits_30d, timestamp_anomaly_detected, pill_count_discrepancy_detected, window_violation_detected, recommended_action, calculated_at) FROM stdin;
807c700c-b85e-4a73-ab8a-710756621aa5	9034f0b1-8d54-4d8a-83c3-f723077062b2	MODERATE	58.77	0	1	1	1	23513	0	4	f	f	f	Phone patient today. Reinforce dosing schedule.	2026-06-10 15:00:03.61338
ad7a2603-6523-46d4-87c0-0b28c245f92e	49ca4358-c32e-44ba-bc52-92d731ea8f5b	LOW	18.75	0	1	1	2	110	0	2	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-10 17:17:41.929457
ee37fe22-ed59-46b2-bc84-5e946ff74838	6b2a9c4a-9845-406f-bf96-5e92606e3309	MODERATE	57.50	0	2	4	8	98	1	2	f	t	f	Monitor closely. Call patient if no confirmation by tomorrow.	2026-06-10 17:17:43.034959
fcfeefd3-d019-4e12-b197-c6d2f507f558	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	100.00	0	6	6	6	23	2	2	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-10 17:17:43.83561
3bb7d21d-a089-4eb0-9e8b-9194f267048c	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	100.00	0	6	6	6	23	2	2	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-10 17:18:10.734279
e2a76d1e-1287-4d2b-b8e4-6753c6e83193	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	100.00	0	6	6	6	23	2	2	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-10 18:08:57.055416
5897e47f-e2ab-42f8-8ae5-0af3fa8e7989	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	99.17	0	7	11	11	23	3	0	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-16 12:06:04.140748
0f8e5eb8-94bf-4568-a1ba-ac0fd081c90d	96fbf0ac-9af5-439b-9730-1d8198fcddea	LOW	18.60	0	0	0	0	120	0	3	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:04.430915
0e633ecb-a05b-429c-89cc-0d4d73f0c239	bfb73641-679c-4161-8587-b48754d8ea9b	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:04.535249
69303f11-9f95-4cf5-bc64-531132e2ca83	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:04.644002
02418418-599e-43ef-9df7-950a8bc8ae51	0ffb9432-cc2a-4814-aa7a-488551497730	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:04.836698
2cdef48d-8a29-4d24-bb36-697b880d1a39	e0f9a10b-4b32-49c7-bb9d-fb8df101610f	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:05.032268
dbb130a7-7cf2-4ef9-a60b-c270b76f7976	46bb83b4-0890-4763-b54e-1ec649d5062b	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:05.1374
d4ad1025-d3e4-4459-9a82-f068fd7c054e	6b86e729-1dd2-4de7-8ec6-4382838cf376	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:05.336088
20db476e-816b-41e7-8dbe-f3b52981b583	35340b74-bb21-41dd-95e1-c27d7531a959	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:05.438738
d7a99d56-780b-42f0-9575-9d2fb043776a	6b2a9c4a-9845-406f-bf96-5e92606e3309	CRITICAL	96.55	0	5	7	11	100	1	2	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-16 12:06:05.635609
aa63170e-e964-41b9-9838-8dc503b91beb	49ca4358-c32e-44ba-bc52-92d731ea8f5b	CRITICAL	94.72	0	5	6	7	111	0	2	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-16 12:06:05.832163
73f540df-6eaf-4cc7-9803-ee86c13e3b46	dce2bd61-cee2-4aac-a559-d537bbd6d175	CRITICAL	95.42	0	5	5	5	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-16 12:06:06.036886
72037e71-7295-4596-8a9d-0a7301f56d14	9c3bfdce-a0a2-4d3c-8c00-41988527b119	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-16 12:06:06.235064
b8b56276-2a0c-4cbe-a25c-4d9baa89dbc5	8009c775-9249-45bf-8f23-5270418f75d1	CRITICAL	95.05	0	2	2	2	3842	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-16 12:06:06.34687
1b078256-5b37-4fc6-b70d-42859a783beb	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	99.17	0	6	12	12	23	3	0	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 10:58:52.454973
7e672591-36d4-4f8a-a17b-ce7a5806ef2d	96fbf0ac-9af5-439b-9730-1d8198fcddea	LOW	18.60	0	0	0	0	120	0	3	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:52.73879
c0a1ce94-6f1d-4ac5-b9e5-05ec5ea36aeb	bfb73641-679c-4161-8587-b48754d8ea9b	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:52.850329
1e14d7e3-bffe-424f-a562-532f193d2823	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:53.048778
d762c0b7-913d-404c-a480-b2f3932131e4	0ffb9432-cc2a-4814-aa7a-488551497730	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:53.245618
825af237-c9f8-479c-9204-14668fdef84a	e0f9a10b-4b32-49c7-bb9d-fb8df101610f	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:53.438685
9386e6fb-f834-46ac-8c83-b3f8afcf48d2	46bb83b4-0890-4763-b54e-1ec649d5062b	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:53.544862
1629ae82-3111-4fb2-98ac-5de07b0a5dc3	6b86e729-1dd2-4de7-8ec6-4382838cf376	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:53.654881
ae7bb159-a0b0-4b46-9637-1ff3300b0bdb	35340b74-bb21-41dd-95e1-c27d7531a959	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:53.853891
bfcc98fa-886f-45a6-b628-f8dcf39daa36	6b2a9c4a-9845-406f-bf96-5e92606e3309	CRITICAL	95.30	0	6	8	12	100	1	1	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 10:58:54.046873
e5993f91-8435-4b14-a806-b0c9c3cd6294	49ca4358-c32e-44ba-bc52-92d731ea8f5b	MODERATE	57.20	0	6	6	8	111	0	1	f	f	f	Phone patient today. Reinforce dosing schedule.	2026-06-18 10:58:54.245025
a7ccedd5-febf-42c1-9e83-f7f82b4522fe	dce2bd61-cee2-4aac-a559-d537bbd6d175	CRITICAL	95.42	0	6	6	6	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 10:58:54.349871
b517bb88-dd34-41d2-8cbd-57ab6e370e40	9c3bfdce-a0a2-4d3c-8c00-41988527b119	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 10:58:54.547269
2fcdd99a-bf40-4c91-b9a5-2b55b6d3902a	8009c775-9249-45bf-8f23-5270418f75d1	CRITICAL	95.12	0	3	3	3	3842	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 10:58:54.653273
692c5ac3-8f24-475c-8e95-068d12ab9425	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	99.17	0	6	12	12	23	3	0	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 19:29:20.742932
65a2bc66-320a-4815-9b0d-51a821ce8fdd	96fbf0ac-9af5-439b-9730-1d8198fcddea	LOW	18.60	0	0	0	0	120	0	3	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:20.899543
c1723dfd-fe14-4b98-97bd-20ce7c49d683	bfb73641-679c-4161-8587-b48754d8ea9b	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:21.093975
cb5222e9-2944-461a-a998-5f411436c781	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:21.291796
94ffda24-38a5-4c4a-ae02-f354b859710b	0ffb9432-cc2a-4814-aa7a-488551497730	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:21.490397
16527d88-ab79-4aff-ac4e-f33307585d73	e0f9a10b-4b32-49c7-bb9d-fb8df101610f	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:21.692363
37897e7d-8bbf-4015-9e64-26659e13203c	46bb83b4-0890-4763-b54e-1ec649d5062b	CRITICAL	95.42	0	1	1	1	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 19:29:21.89318
9b306b42-97f3-43c2-bedd-ef43670f5b41	6b86e729-1dd2-4de7-8ec6-4382838cf376	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:22.098802
61415fe1-895e-42c6-8941-eeaddf41bec5	35340b74-bb21-41dd-95e1-c27d7531a959	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:22.299146
4af470fe-0e87-4112-b3a8-dffed107c8f8	6b2a9c4a-9845-406f-bf96-5e92606e3309	CRITICAL	95.30	0	6	8	12	100	1	1	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 19:29:22.49639
30e7fa09-6083-49ce-abda-f27feeb07dbe	49ca4358-c32e-44ba-bc52-92d731ea8f5b	MODERATE	57.20	0	6	6	8	111	0	1	f	f	f	Phone patient today. Reinforce dosing schedule.	2026-06-18 19:29:22.697424
8ff23935-e6fb-455a-9d71-304fac3008aa	dce2bd61-cee2-4aac-a559-d537bbd6d175	CRITICAL	95.42	0	6	6	6	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 19:29:22.891311
e0c8d7cd-e125-4237-9a08-2eddae5cbc7f	9c3bfdce-a0a2-4d3c-8c00-41988527b119	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 19:29:23.095281
27e9905b-0896-4469-8f74-ec9d9ecbd3bf	8009c775-9249-45bf-8f23-5270418f75d1	CRITICAL	95.12	0	3	3	3	3842	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 19:29:23.29435
4031d082-c7d7-4141-8583-bb05115696e2	bfb73641-679c-4161-8587-b48754d8ea9b	MODERATE	50.00	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	Patient re-engaged after tracing visit. Monitor adherence closely over the next 30 days.	2026-06-18 21:19:09.602864
24bef3f4-bdff-4394-bccb-528bbd1bbafd	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	99.17	0	6	12	12	23	3	0	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 23:06:59.802532
92b2f14c-91de-4f9f-8734-a311d5a2ddd5	96fbf0ac-9af5-439b-9730-1d8198fcddea	LOW	18.60	0	0	0	0	120	0	3	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 23:07:00.099553
289096c1-f97e-49f6-99f0-1285fd428d24	bfb73641-679c-4161-8587-b48754d8ea9b	CRITICAL	95.42	0	1	1	1	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 23:07:00.294431
6b8f33fb-1d93-4103-885e-2438df34f418	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 23:07:00.545447
3a398c31-dedd-4149-9875-071dabd55577	0ffb9432-cc2a-4814-aa7a-488551497730	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 23:07:00.850344
3d07fd99-d2b2-48eb-ab46-693eb0826ecb	e0f9a10b-4b32-49c7-bb9d-fb8df101610f	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 23:07:01.070586
c46e6673-0b26-4f24-85d9-61be2bbaf408	46bb83b4-0890-4763-b54e-1ec649d5062b	CRITICAL	95.42	0	1	1	1	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 23:07:01.449665
f95d65df-0820-4dac-a937-5105a6075017	6b86e729-1dd2-4de7-8ec6-4382838cf376	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 23:07:01.740638
dd63428c-262d-4b13-bf71-37af13e96cc4	35340b74-bb21-41dd-95e1-c27d7531a959	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 23:07:01.901527
047a0574-af5b-4dd3-b735-34d434e82393	6b2a9c4a-9845-406f-bf96-5e92606e3309	CRITICAL	95.30	0	6	8	12	100	1	1	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 23:07:02.100518
cd1ba6d2-c7c9-4cb6-be3e-0078f1467eb3	49ca4358-c32e-44ba-bc52-92d731ea8f5b	MODERATE	57.20	0	6	6	8	111	0	1	f	f	f	Phone patient today. Reinforce dosing schedule.	2026-06-18 23:07:02.388513
33be5f98-5ae5-4f34-9366-46a81b61f164	dce2bd61-cee2-4aac-a559-d537bbd6d175	CRITICAL	95.42	0	6	6	6	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 23:07:02.507665
6f2df8f1-dc0d-4d42-bc54-55529e7014fd	9c3bfdce-a0a2-4d3c-8c00-41988527b119	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-18 23:07:02.699446
03e7dff6-b7ab-42fc-bfb0-6f24f06a72f9	8009c775-9249-45bf-8f23-5270418f75d1	CRITICAL	95.12	0	3	3	3	3842	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-18 23:07:02.90037
2f00b55a-69fb-4311-a4c2-1663a9218e4a	9034f0b1-8d54-4d8a-83c3-f723077062b2	CRITICAL	99.17	0	5	11	12	23	3	0	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-19 04:59:01.841141
79bd079e-740b-4f76-ac8c-d83d982b6f9d	96fbf0ac-9af5-439b-9730-1d8198fcddea	LOW	18.60	0	0	0	0	120	0	3	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-19 04:59:02.13876
f79bf314-cbe3-4aee-832c-3b45e64e9913	bfb73641-679c-4161-8587-b48754d8ea9b	CRITICAL	95.42	0	1	1	1	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-19 04:59:02.33175
b9057239-a92a-49b3-a8f1-477f3b0294b7	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-19 04:59:02.444007
8225c046-ae5a-47be-8d14-2c849ae692f4	0ffb9432-cc2a-4814-aa7a-488551497730	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-19 04:59:02.636277
1579594c-ccca-4ba2-a304-1934d6cb58c6	e0f9a10b-4b32-49c7-bb9d-fb8df101610f	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-19 04:59:02.746041
8d48b320-b0eb-4a66-b138-6016acdb685a	46bb83b4-0890-4763-b54e-1ec649d5062b	CRITICAL	95.42	0	1	1	1	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-19 04:59:02.942658
c115dc4a-5bfb-447a-8cb1-6d269ac274b7	6b86e729-1dd2-4de7-8ec6-4382838cf376	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-19 04:59:03.136853
a384d42e-395e-4554-ac56-e72f1d26608c	35340b74-bb21-41dd-95e1-c27d7531a959	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-19 04:59:03.333869
3b0f0591-0f5b-4859-9d3b-fa3e2f4a7083	6b2a9c4a-9845-406f-bf96-5e92606e3309	CRITICAL	95.50	0	5	8	11	100	1	2	f	t	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-19 04:59:03.533787
f79b840e-0853-495b-b423-cd8e0f566b07	49ca4358-c32e-44ba-bc52-92d731ea8f5b	MODERATE	57.25	0	5	6	8	110	0	1	f	f	f	Phone patient today. Reinforce dosing schedule.	2026-06-19 04:59:03.729713
0bd5094b-b736-46f0-a8f9-03559ec41022	dce2bd61-cee2-4aac-a559-d537bbd6d175	CRITICAL	95.42	0	5	6	6	120	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-19 04:59:03.841139
386026cb-596b-4b26-8aba-58b26533e763	9c3bfdce-a0a2-4d3c-8c00-41988527b119	LOW	18.60	0	0	0	0	120	0	4	f	f	f	Patient is stable. Routine follow-up applies.	2026-06-19 04:59:04.037571
f69331e9-15bd-411f-80fb-e3e8d7c9660d	8009c775-9249-45bf-8f23-5270418f75d1	CRITICAL	95.12	0	3	3	3	3842	0	4	f	f	f	Immediate home visit required. Notify facility provider and supervisor.	2026-06-19 04:59:04.14583
0f8bc514-ec33-4133-9353-f4e3f2133acc	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	MODERATE	50.00	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	Patient re-engaged after tracing visit. Monitor adherence closely over the next 30 days.	2026-06-19 07:21:33.833021
47cdb18b-61fa-4d49-bff5-85319ca964cd	dce2bd61-cee2-4aac-a559-d537bbd6d175	MODERATE	50.00	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	Patient re-engaged after tracing visit. Monitor adherence closely over the next 30 days.	2026-06-19 07:28:23.631175
\.


--
-- Data for Name: alerts; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.alerts (id, patient_id, chw_id, provider_id, supervisor_id, alert_type, severity, title, message, is_read, is_resolved, resolved_at, created_at, resolved_by, escalated_at) FROM stdin;
e36c3099-01cc-4fa1-9add-3955b9477313	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-11.	f	t	2026-06-19 07:32:59.102441	2026-06-11 08:44:10.848357	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.558908
5b841fb9-bced-48d7-bc87-2ab673cfffee	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_CONFIRMED	CRITICAL	LTFU Confirmed — Immaculee Nyiraneza	Patient Immaculee Nyiraneza (PT-B40E538E) is officially Lost to Follow-Up after 22 days without contact since 2026-05-20. Status escalated to supervisor. Immediate action required.	t	t	2026-06-11 21:38:32.526743	2026-06-11 20:21:06.89197	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
bf8bb6cc-7299-471a-8aef-30cb37a28c44	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Marie Uwimana	Patient Marie Uwimana (PT-BD5C2489) missed their facility appointment on 2026-05-07. Days since missed: 35. Reason: LOST_TO_FOLLOWUP. Please conduct a tracing visit.	f	t	2026-06-19 07:31:47.931992	2026-06-11 09:54:19.786228	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.60368
007adfa0-9a5c-48b4-8b19-29e0e01ad16d	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-12.	f	t	2026-06-19 07:32:11.284884	2026-06-12 18:51:01.051484	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-15 09:00:00.337922
4f11272d-d22c-49a4-b0f1-ff6f448e5ea8	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-14.	f	t	2026-06-19 07:32:15.620357	2026-06-14 12:20:59.443612	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.227373
58bca98a-b760-48cb-9f72-85fce5446298	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-10.	f	t	2026-06-19 07:30:50.869963	2026-06-10 10:55:25.715662	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-12 19:00:00.55249
26b9ad4e-dc5a-4397-9cf1-0ba281659037	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jean Damascene	Risk score 100.0/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:30:58.166441	2026-06-10 18:08:57.068062	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-12 19:00:00.6526
6b577bc2-d2ca-4a09-944d-2260062cacaf	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-14.	f	t	2026-06-19 07:33:16.628712	2026-06-14 12:21:01.642975	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.328654
69d4dc6f-889d-4bcb-8e0f-e2f94692ee48	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-14.	f	t	2026-06-19 07:33:51.477805	2026-06-14 12:21:01.04529	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.325085
3985f74e-a8a8-4382-80f5-cbbac7e5ab3a	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-14.	f	t	2026-06-19 07:34:19.082514	2026-06-14 12:21:00.644752	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.32272
52ed3724-27f3-46d7-a486-205e56d2a97f	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-12.	f	t	2026-06-19 07:32:18.480448	2026-06-12 18:50:59.55461	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-15 09:00:00.249877
da44893e-24d7-474e-aa8b-00216de13fc7	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-12.	f	t	2026-06-19 07:32:21.734053	2026-06-12 18:51:01.551859	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-15 09:00:00.338715
ba8326ce-8ca1-447d-9602-102b23da36cd	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-11.	f	t	2026-06-19 07:31:16.903873	2026-06-11 09:00:02.948585	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.602338
d9f2a6af-b909-4005-b7cb-fb8590591cbc	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	LTFU_CONFIRMED	CRITICAL	LTFU Confirmed — Innocent Nishimwe	Patient Innocent Nishimwe (PT-F56814DE) is officially Lost to Follow-Up after 5 days without contact since 2026-06-06. Status escalated to supervisor. Immediate action required.	t	t	2026-06-19 07:31:26.605377	2026-06-11 09:53:01.779784	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.603344
4fb5d4e9-70eb-4811-8f43-8879bd6af36f	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Innocent Nishimwe	Patient Innocent Nishimwe (PT-F56814DE) missed their facility appointment on 2026-06-06. Days since missed: 5. Reason: MISSED_APPOINTMENT. Please conduct a tracing visit.	t	t	2026-06-19 07:31:44.765494	2026-06-11 09:52:25.770532	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.60282
417a73f9-a8e8-46bf-b488-ed4b99ec8220	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-12.	f	t	2026-06-19 07:32:25.684102	2026-06-12 18:51:00.351738	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-15 09:00:00.336418
3cd27730-44c7-4299-acf3-9079470ae243	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_CONFIRMED	CRITICAL	LTFU Confirmed — Marie Uwimana	Patient Marie Uwimana (PT-BD5C2489) is officially Lost to Follow-Up after 35 days without contact since 2026-05-07. Status escalated to supervisor. Immediate action required.	f	t	2026-06-19 07:32:51.730814	2026-06-11 09:54:20.879546	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.603904
7fe16af1-cd20-43d1-8b21-d92f75559d6f	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-11.	f	t	2026-06-19 07:32:54.659701	2026-06-11 09:00:02.860451	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.601653
0622583c-cddb-498c-a1b5-2d8ddf37c08d	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-11.	f	t	2026-06-19 07:32:45.854124	2026-06-11 18:58:17.962932	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.604164
72fc5c50-6d18-48c3-a05f-5d1b4d7e69b7	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-15.	f	t	2026-06-19 07:33:11.422472	2026-06-15 08:35:44.583085	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.420999
492a8ef4-e6bb-489b-880b-9a12656e6a8f	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-15.	f	t	2026-06-19 07:33:55.075567	2026-06-15 09:00:45.139456	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.422571
73ae51ba-781b-4b82-9ffa-6740584e9204	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Jean Damascene	Patient Jean Damascene (PT-EEF83CCE) missed their facility appointment on 2026-06-10. Days since missed: 5. Reason: MISSED_REFILL. Please conduct a tracing visit.	f	t	2026-06-19 07:34:10.769363	2026-06-15 20:03:00.634562	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.423266
07ecead3-6d3e-48ec-bdfa-290bb16ae1ff	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-15.	f	t	2026-06-19 07:34:15.367563	2026-06-15 09:00:45.243187	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.422857
223e6c45-633d-445e-8ce2-5352376cc28c	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-16.	f	t	2026-06-19 07:34:22.878457	2026-06-16 10:01:01.4868	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 11:00:00.240259
916c9ae4-4ed6-40f7-86e3-cc065bd489e9	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Eric Mugisha	Patient Eric Mugisha (PT-F952B170) missed their facility appointment on 2026-05-26. Days since missed: 20. Reason: MISSED_APPOINTMENT. Please conduct a tracing visit.	f	t	2026-06-19 07:34:31.064605	2026-06-15 20:15:57.616075	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.424928
08b66a89-d420-4704-bbf2-619a4e580ca2	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-16.	f	t	2026-06-19 07:34:37.601535	2026-06-16 10:01:01.992206	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 11:00:00.339454
93aa9a0d-ba1c-4a25-9536-bfe999682edb	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-16.	f	t	2026-06-19 07:34:45.381807	2026-06-16 10:01:01.888349	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 11:00:00.243075
c50fcad3-8d41-4794-aa05-b1dcdd20a069	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jean Damascene	Risk score 99.2/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:34:48.728745	2026-06-16 12:06:04.234039	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 15:00:00.441661
100acc93-79c7-4a7a-a3a4-4a3867058a4f	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-18.	f	f	\N	2026-06-18 08:44:41.321808	\N	2026-06-20 19:00:00.42785
daf4a95e-8352-4d0b-a9b3-90aa4fab6198	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Jeanne Mukamana	Patient Jeanne Mukamana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-18.	f	t	2026-06-19 07:36:07.731219	2026-06-18 08:45:35.026486	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
dae38cb5-ab6a-4039-a0bd-f73e16ea7d20	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-16.	f	f	\N	2026-06-16 10:01:02.28786	\N	2026-06-18 11:00:00.34024
9e14d34f-c836-40a7-9786-a27c37cf2ec9	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jeanne Mukamana	Patient Jeanne Mukamana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-16.	f	f	\N	2026-06-16 10:01:02.392664	\N	2026-06-18 11:00:00.340612
2d7cbd2e-5d76-4b36-a265-6929bd6f128d	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-18.	f	f	\N	2026-06-18 09:00:34.440406	\N	2026-06-20 19:00:00.520973
c1ddd9a3-11ef-4254-81b4-b730da9e118a	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Immaculee Nyiraneza	Risk score 96.5/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-16 12:06:05.647519	\N	2026-06-18 15:00:00.50004
a835b777-2d5f-4ff7-b160-315f77a4fac5	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Marie Uwimana	Risk score 94.7/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-16 12:06:05.848812	\N	2026-06-18 15:00:00.501214
1cc5e616-bf19-4322-904f-bd6bc4a98bbf	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Eric Mugisha	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-16 12:06:06.050837	\N	2026-06-18 15:00:00.59763
3842ec68-e08e-44ff-854e-dae509740289	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jeanne Mukamana	Risk score 95.0/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-16 12:06:06.431247	\N	2026-06-18 15:00:00.59926
b2f51034-7893-49cf-a13c-442244f28084	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Immaculee Nyiraneza	Patient Immaculee Nyiraneza (PT-B40E538E) missed their facility appointment on 2026-05-20. Days since missed: 22. Reason: LOST_TO_FOLLOWUP. Please conduct a tracing visit.	f	t	2026-06-19 07:32:29.21166	2026-06-11 20:20:52.392749	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.696635
4d6283b4-d426-45e9-a2b6-2a6936455898	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Pierre Hakizimana	Patient Pierre Hakizimana (PT-5A587173) missed their facility appointment on 2026-06-04. Days since missed: 7. Reason: MISSED_APPOINTMENT. Please conduct a tracing visit.	t	t	2026-06-19 07:32:35.004981	2026-06-11 20:20:51.491448	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.696062
0849df54-f44b-44ea-8a9a-5067eb819922	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Valentine Niyonsenga	Patient Valentine Niyonsenga (PT-42B6C368) missed their facility appointment on 2026-06-09. Days since missed: 2. Reason: MISSED_REFILL. Please conduct a tracing visit.	t	t	2026-06-19 07:32:41.880944	2026-06-11 20:20:50.186856	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 14:00:00.604469
b1c4fb0f-40d3-46fb-8137-ce37f79abff5	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-15.	f	t	2026-06-19 07:33:28.755252	2026-06-15 09:00:44.94341	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.42221
ebad179d-bd42-496e-b960-3b6fba88f3bc	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jeanne Mukamana	Patient Jeanne Mukamana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-15.	f	t	2026-06-19 07:33:33.22971	2026-06-15 08:59:44.961186	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 09:00:00.421699
299c7ac4-1016-46d4-b07d-07f57b211e4b	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-18.	f	t	2026-06-18 10:55:45.63719	2026-06-18 09:00:34.826194	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
e88f0e4e-923e-4b19-b6f8-20253f4b2d9f	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jeanne Mukamana	Risk score 95.1/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-18 20:32:10.684269	2026-06-18 19:29:23.306424	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N
eb4cef24-224f-42f8-a350-4f527141db63	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jeanne Mukamana	Risk score 95.1/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-18 23:08:42.634647	2026-06-18 23:07:02.911765	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
c66421b4-6026-49bd-b3a4-75b89ff58471	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Eric Mugisha	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-18 23:08:46.3241	2026-06-18 23:07:02.522121	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
ee8c8397-31a0-4815-ae6b-ce60a6afe2b0	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Immaculee Nyiraneza	Risk score 95.3/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-18 23:09:01.436383	2026-06-18 19:29:22.503822	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
2ee89a2b-1ba6-4ff0-ae0d-2b18de9b2fa6	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Eric Mugisha	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:29:10.837432	2026-06-19 04:59:03.846913	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
785d102c-163f-4192-ad77-cf59eac2f2c2	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Immaculee Nyiraneza	Risk score 95.5/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:29:43.130382	2026-06-19 04:59:03.539859	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
d7bd992c-1a4b-487d-80e2-1eae40107a8d	46bb83b4-0890-4763-b54e-1ec649d5062b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — SMS	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:30:05.185093	2026-06-19 04:59:02.948187	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
4d606f7f-8148-469e-847b-6f634c611936	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Immaculee Nyiraneza	Risk score 95.3/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:30:10.238527	2026-06-18 23:07:02.110318	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
ed1613ee-d473-482a-8e46-b1bcd41f7e01	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_CONFIRMED	CRITICAL	LTFU Confirmed — Immaculee Nyiraneza	Patient Immaculee Nyiraneza (PT-B40E538E) is officially Lost to Follow-Up after 30 days without contact since 2026-05-20. Status escalated to supervisor. Immediate action required.	f	t	2026-06-19 07:30:13.249449	2026-06-19 07:17:55.241083	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
2a58936c-49ed-474e-a366-995d390fcf09	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Eric Mugisha	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:35:27.846806	2026-06-18 19:29:22.898571	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
13faca3e-5a90-4cad-9543-578ef8700259	46bb83b4-0890-4763-b54e-1ec649d5062b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — SMS	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:35:35.759685	2026-06-18 19:29:21.900184	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
b36c451d-c35d-4d34-a232-dc954b5546d7	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jean Damascene	Risk score 99.2/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:35:39.042399	2026-06-18 19:29:20.758114	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
8ff75153-2ed7-40bc-a43a-cfab1127154a	46bb83b4-0890-4763-b54e-1ec649d5062b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	WARNING	Missed Dose — SMS	Patient SMS missed their EFV dose scheduled for 2026-06-18.	f	t	2026-06-19 07:35:41.599745	2026-06-18 14:53:18.597831	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
951ae55b-6828-4677-ab80-c7c1670e0bab	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jeanne Mukamana	Risk score 95.1/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:35:45.371296	2026-06-18 10:58:54.745486	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
9f7af628-30a2-462e-a584-cbd5a9f5476c	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Eric Mugisha	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:35:51.057768	2026-06-18 10:58:54.355159	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
98a79b8e-72c4-44e6-877f-8236266a2d75	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Immaculee Nyiraneza	Risk score 95.3/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:35:57.283452	2026-06-18 10:58:54.053928	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
21179ab7-dd00-4df8-946d-0409aabbe7f8	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jean Damascene	Risk score 99.2/100. Immediate home visit required. Notify facility provider and supervisor.	f	t	2026-06-19 07:36:04.226502	2026-06-18 10:58:52.54986	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
f619b4e1-e1e4-498d-af14-2789d99a4b88	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-18.	f	f	\N	2026-06-18 09:00:34.54236	\N	2026-06-20 19:00:00.713751
b0aeed68-0006-47cb-ab81-cfdbf8a3269c	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Pierre Hakizimana	Patient Pierre Hakizimana missed their TDF dose scheduled for 2026-06-18.	f	f	\N	2026-06-18 23:04:25.81086	\N	2026-06-21 20:00:00.837461
627ef81b-5525-4ef2-b77e-9014a665a33f	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_CONFIRMED	CRITICAL	LTFU Confirmed — Immaculee Nyiraneza	Patient Immaculee Nyiraneza (PT-B40E538E) is officially Lost to Follow-Up after 30 days without contact since 2026-05-20. Status escalated to supervisor. Immediate action required.	f	t	2026-06-19 07:29:34.233083	2026-06-19 07:17:56.130953	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
840025ad-6d0e-4b9f-acb2-9125f0697396	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	56f20695-337b-42d9-becf-f7c675724aa6	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Eric Mugisha	Tracing task for patient Eric Mugisha (PT-F952B170) was resolved by CHW Jean Pierre. Outcome: PATIENT_FOUND. Plan: patient agreed to start treatment again next Monday	f	f	\N	2026-06-19 07:28:23.490976	\N	2026-06-21 20:00:01.050297
85235810-7541-400f-9590-569d8831fc83	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	1b8b109f-ef8f-44bb-b7a5-484a09d39769	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Eric Mugisha	Tracing task for patient Eric Mugisha (PT-F952B170) was resolved by CHW Jean Pierre. Outcome: PATIENT_FOUND. Plan: patient agreed to start treatment again next Monday	f	f	\N	2026-06-19 07:28:23.533394	\N	2026-06-21 20:00:01.053541
27240019-4aaa-4c7a-8ba1-46d381091c7d	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	739f4912-a76b-4515-9f77-8195d0f0393e	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Eric Mugisha	Tracing task for patient Eric Mugisha (PT-F952B170) was resolved by CHW Jean Pierre. Outcome: PATIENT_FOUND. Plan: patient agreed to start treatment again next Monday	f	f	\N	2026-06-19 07:28:23.535078	\N	2026-06-21 20:00:01.137337
092836f9-7a33-4da0-8b5d-5379fb4f55ea	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Eric Mugisha	Patient Eric Mugisha (PT-F952B170) missed their facility appointment on 2026-05-26. Days since missed: 24. Reason: MISSED_APPOINTMENT. Please conduct a tracing visit.	f	t	2026-06-19 07:28:57.134453	2026-06-19 06:00:01.553598	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
ef7a9785-4a0e-4fba-9e54-bf8ea75a987a	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	LTFU_CONFIRMED	CRITICAL	LTFU Confirmed — Immaculee Nyiraneza	Patient Immaculee Nyiraneza (PT-B40E538E) is officially Lost to Follow-Up after 30 days without contact since 2026-05-20. Status escalated to supervisor. Immediate action required.	f	t	2026-06-19 07:29:21.03111	2026-06-19 07:17:56.632012	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
90b0209a-6f0d-4ab3-81d6-38e5727cfc95	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	LTFU_TRACING	WARNING	LTFU Tracing Required — Innocent Nishimwe	Patient Innocent Nishimwe (PT-F56814DE) missed their facility appointment on 2026-06-01. Days since missed: 18. Reason: MISSED_APPOINTMENT. Please conduct a tracing visit.	f	t	2026-06-19 07:29:56.336754	2026-06-19 06:00:00.070934	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N
d6e4fe30-52c1-4f7a-a60c-1a4b87f40b7b	46bb83b4-0890-4763-b54e-1ec649d5062b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	WARNING	Missed Dose — SMS	Patient SMS missed their EFV dose scheduled for 2026-06-19.	f	f	\N	2026-06-19 23:50:16.828146	\N	\N
f851b4fa-fe59-4446-8e79-6252583ee6c4	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-20.	f	f	\N	2026-06-20 16:16:50.759481	\N	\N
c955c8a5-73e1-406a-9642-e3d96727bfd5	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-20.	f	f	\N	2026-06-20 16:16:55.362	\N	\N
79e1e785-a07c-4ac0-b721-04ece9550ad6	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-20.	f	f	\N	2026-06-20 16:16:56.05869	\N	\N
c0cf209f-410c-425a-b05c-85bedd5e3e91	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-20.	f	f	\N	2026-06-20 16:16:56.664265	\N	\N
8d273293-1711-4198-ba02-b35524c02817	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Jeanne Mukamana	Patient Jeanne Mukamana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-20.	f	f	\N	2026-06-20 16:16:57.159955	\N	\N
7787e01c-3487-4b15-9108-9847b69a764a	46bb83b4-0890-4763-b54e-1ec649d5062b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	WARNING	Missed Dose — SMS	Patient SMS missed their EFV dose scheduled for 2026-06-20.	f	f	\N	2026-06-20 16:16:57.662302	\N	\N
925ded77-6c14-4737-aac0-f7ab6f303945	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Pierre Hakizimana	Patient Pierre Hakizimana missed their TDF dose scheduled for 2026-06-20.	f	f	\N	2026-06-20 16:16:58.163713	\N	\N
5027e728-4932-4509-a58e-7ea67eeabd1f	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-21.	f	f	\N	2026-06-21 19:47:50.242633	\N	\N
e78e4061-7347-49ad-8861-47401b377536	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Marie Uwimana	Patient Marie Uwimana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-21.	f	f	\N	2026-06-21 19:47:52.634798	\N	\N
d8400ed9-1062-45bc-abd5-f455bb1cc12a	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-21.	f	f	\N	2026-06-21 19:47:52.938584	\N	\N
ed175c67-e543-40b6-9d32-669e3667357a	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-21.	f	f	\N	2026-06-21 19:47:53.436954	\N	\N
d459b69a-c0fa-40e9-a539-dc1570a26f9e	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Jeanne Mukamana	Patient Jeanne Mukamana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-21.	f	f	\N	2026-06-21 19:47:53.943455	\N	\N
05f89421-8b6e-4106-ab4d-89c68c37e685	46bb83b4-0890-4763-b54e-1ec649d5062b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	WARNING	Missed Dose — SMS	Patient SMS missed their EFV dose scheduled for 2026-06-21.	f	f	\N	2026-06-21 19:47:55.034802	\N	\N
cf2b7c9e-33e9-4e4a-8d3c-ea9aba637794	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	WARNING	Missed Dose — Pierre Hakizimana	Patient Pierre Hakizimana missed their TDF dose scheduled for 2026-06-21.	f	f	\N	2026-06-21 19:47:55.940284	\N	\N
a8bc5fdd-81b6-463e-a549-10e9ae6b1b90	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	56f20695-337b-42d9-becf-f7c675724aa6	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Pierre Hakizimana	Tracing task for patient Pierre Hakizimana (PT-5A587173) was resolved by CHW Alice Uwimana. Outcome: PATIENT_FOUND. Plan: patient agreed to restart treatment	f	f	\N	2026-06-18 21:19:09.210487	\N	2026-06-21 20:00:00.64252
414eb4a8-d324-4eb8-b67e-82719000c9d6	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	1b8b109f-ef8f-44bb-b7a5-484a09d39769	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Pierre Hakizimana	Tracing task for patient Pierre Hakizimana (PT-5A587173) was resolved by CHW Alice Uwimana. Outcome: PATIENT_FOUND. Plan: patient agreed to restart treatment	f	f	\N	2026-06-18 21:19:09.402163	\N	2026-06-21 20:00:00.835516
c47d3d7c-7a74-45c1-80af-073145bc65b0	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	739f4912-a76b-4515-9f77-8195d0f0393e	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Pierre Hakizimana	Tracing task for patient Pierre Hakizimana (PT-5A587173) was resolved by CHW Alice Uwimana. Outcome: PATIENT_FOUND. Plan: patient agreed to restart treatment	f	f	\N	2026-06-18 21:19:09.502211	\N	2026-06-21 20:00:00.836796
6e8a3a57-f072-477c-b9d2-3a9d4a9abf44	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	02a4d824-e859-45e5-9092-40dc69fa4c89	56f20695-337b-42d9-becf-f7c675724aa6	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Valentine Niyonsenga	Tracing task for patient Valentine Niyonsenga (PT-42B6C368) was resolved by CHW Alice Uwimana. Outcome: PATIENT_FOUND. Plan: Patient Agreed to restart treatment with transport convin	f	f	\N	2026-06-19 07:21:33.641791	\N	2026-06-21 20:00:01.138415
8a059d40-ffee-4090-852c-d560a4169be3	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jean Damascene	Risk score 99.2/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-18 23:06:59.8967	\N	2026-06-21 20:00:00.838895
9932c980-c851-40ff-a900-a559e9dcf627	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Pierre Hakizimana	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-18 23:07:00.300973	\N	2026-06-21 20:00:00.84125
027ca5f4-4104-4dc2-a244-955d88ba1567	46bb83b4-0890-4763-b54e-1ec649d5062b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — SMS	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-18 23:07:01.529858	\N	2026-06-21 20:00:00.856223
fce4344b-9614-4a80-a251-c992e2969643	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jean Damascene	Risk score 99.2/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-19 04:59:01.934776	\N	2026-06-21 20:00:00.950316
b32c1933-be2c-4fb6-bf02-4d4aded62043	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Pierre Hakizimana	Risk score 95.4/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-19 04:59:02.337203	\N	2026-06-21 20:00:00.95457
a0fd09bb-34ba-45a8-9368-92cbcdf4bd34	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	EARLY_WARNING	CRITICAL	CRITICAL Risk — Jeanne Mukamana	Risk score 95.1/100. Immediate home visit required. Notify facility provider and supervisor.	f	f	\N	2026-06-19 04:59:04.23469	\N	2026-06-21 20:00:01.036001
378111f9-fbeb-4821-939c-0e99b403cbb3	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	LTFU_TRACING	CRITICAL	LTFU Tracing Required — Innocent Nishimwe	Patient Innocent Nishimwe (PT-F56814DE) missed their facility appointment on 2026-06-01. Days since missed: 18. Reason: MISSED_APPOINTMENT. Please conduct a tracing visit.	f	f	\N	2026-06-19 06:00:01.65439	\N	2026-06-21 20:00:01.047685
4fc4da99-12f2-4ca8-8fca-f6f6d7f47e29	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	02a4d824-e859-45e5-9092-40dc69fa4c89	1b8b109f-ef8f-44bb-b7a5-484a09d39769	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Valentine Niyonsenga	Tracing task for patient Valentine Niyonsenga (PT-42B6C368) was resolved by CHW Alice Uwimana. Outcome: PATIENT_FOUND. Plan: Patient Agreed to restart treatment with transport convin	f	f	\N	2026-06-19 07:21:33.73397	\N	2026-06-21 20:00:01.139562
112d1a5b-9095-4eae-b5a0-fba27a59e395	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	02a4d824-e859-45e5-9092-40dc69fa4c89	739f4912-a76b-4515-9f77-8195d0f0393e	\N	LTFU_TRACING_RESOLVED	CRITICAL	Tracing Resolved — Valentine Niyonsenga	Tracing task for patient Valentine Niyonsenga (PT-42B6C368) was resolved by CHW Alice Uwimana. Outcome: PATIENT_FOUND. Plan: Patient Agreed to restart treatment with transport convin	f	f	\N	2026-06-19 07:21:33.73724	\N	2026-06-21 20:00:01.234763
59321ad6-f580-4bad-8352-84da3ca161d9	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Immaculee Nyiraneza	Patient Immaculee Nyiraneza missed their Rifafour (RHZE) dose scheduled for 2026-06-19.	f	f	\N	2026-06-19 08:30:01.764125	\N	2026-06-21 20:00:01.241697
a89ac401-b516-4037-8e45-3264bea79fc9	8009c775-9249-45bf-8f23-5270418f75d1	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jeanne Mukamana	Patient Jeanne Mukamana missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-19.	f	f	\N	2026-06-19 08:45:01.682759	\N	2026-06-21 20:00:01.244399
0b0f99c9-1194-4a9b-ae25-f45bd82f8bc7	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Pierre Hakizimana	Patient Pierre Hakizimana missed their TDF dose scheduled for 2026-06-19.	f	f	\N	2026-06-19 08:45:01.872544	\N	2026-06-21 20:00:01.250039
59c02911-f2c1-4387-bcb3-682f3324a905	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Jean Damascene	Patient Jean Damascene missed their TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE dose scheduled for 2026-06-19.	f	f	\N	2026-06-19 09:00:01.6739	\N	2026-06-21 20:00:01.258166
d5eedc49-c17b-4de4-8859-ddad7c1d6a6a	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	\N	\N	MISSED_DOSE	CRITICAL	Missed Dose — Eric Mugisha	Patient Eric Mugisha missed their Tenofovir/Lamivudine/Dolutegravir (TLD) dose scheduled for 2026-06-19.	f	f	\N	2026-06-19 09:00:01.972636	\N	2026-06-21 20:00:01.339203
\.


--
-- Data for Name: audit_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.audit_logs (id, user_id, action, target_table, target_id, ip_address, details, created_at) FROM stdin;
b8d7d8c2-b773-475f-a4e8-e6ede5038bea	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-05-30 21:37:21.511537
9c5e75f1-6006-449c-986e-4225fb8db831	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-05-30 21:39:19.529832
ead806a1-877f-4ab7-a267-1a5db258ecbe	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-05-30 21:44:24.416915
8471cd5c-dd37-4206-a877-93cbfd2c116d	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-05-30 21:45:12.520797
7f0e543b-eaee-4e98-8f21-83a14b5148a1	4756ff76-d053-442f-810f-361073e5a6e1	RECORD_VISIT	home_visits	83287823-040f-456c-8fcd-edd7a32bb755	\N	\N	2026-05-30 21:46:26.617562
364ff285-073c-4120-9597-c08cd7f8e69b	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-05-30 21:48:02.9141
5e88933f-82cc-4863-8850-2736efcf4242	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-05-30 21:51:23.92065
d8c1993a-648c-4ff1-871f-ddae12be0ede	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-05-30 21:53:00.728687
7ff33d3c-7e9f-4ddd-9f52-c186cb53be54	\N	LOGIN	system_users	3414701f-3738-4f15-8e0e-e84cd8692388	\N	\N	2026-05-30 21:57:40.022893
61c6a266-0bfb-4739-9e42-8c4ba6331f00	3414701f-3738-4f15-8e0e-e84cd8692388	CHANGE_PASSWORD	system_users	3414701f-3738-4f15-8e0e-e84cd8692388	\N	\N	2026-05-30 22:00:21.123343
7a177ed3-522a-4724-9ff1-6ee3610d0502	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-02 16:52:42.902795
eec89dbe-11fa-4149-8f9a-e9702df69493	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-02 16:56:33.612666
2fcac19b-4c15-4901-ba70-38b893517585	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-02 16:57:17.103359
dd97905e-04ce-4fb2-9b43-88637cdd83c6	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	3414701f-3738-4f15-8e0e-e84cd8692388	\N	\N	2026-06-02 16:57:35.1048
f5c7a551-c5a6-4c32-8a11-5298857f98a2	\N	LOGIN	system_users	3414701f-3738-4f15-8e0e-e84cd8692388	\N	\N	2026-06-02 17:00:17.111634
bfcf31cc-3f23-4d3c-abd7-2678d1e0db85	3414701f-3738-4f15-8e0e-e84cd8692388	CHANGE_PASSWORD	system_users	3414701f-3738-4f15-8e0e-e84cd8692388	\N	\N	2026-06-02 17:01:11.201197
24e6f121-2312-4339-908d-2db22e12606f	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-02 17:02:29.4009
03619c03-9e7d-40b9-8225-dc8ca23dc5e6	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-02 17:04:19.907579
da543f73-c328-4fbd-97bf-1986c6cfba24	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-02 17:14:07.903741
fd85b683-a046-4fde-9040-3ba55ff70f72	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-02 17:58:43.92079
a45240c0-0b02-4268-8667-82bb9cf3f853	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-02 17:59:52.728774
80180973-d5b7-4675-ab76-1aed195b5e75	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-03 08:16:14.361554
92b5f17a-09a4-44c0-ad3b-ca29cee6b50d	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-03 08:20:17.857374
7d9b08d6-98e3-451c-be9f-94d2580d561b	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-03 10:36:11.782895
1323fbe2-3b42-4486-b384-94b6d8b0a5ec	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-05 09:57:28.115833
e2750815-5f4f-418b-90d9-f315f94d0adc	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-05 09:58:27.811922
3f38bc38-eac6-44d7-900b-ad2026d124ca	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-05 10:03:24.713981
7ef1d7bc-9578-4e05-a9ff-ef1dced8ff29	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-05 11:30:06.882533
fa13d58c-1e8c-469e-83ff-f2c6df03e1ab	3b8dafc7-ab88-49a0-8699-686d991df1ff	CREATE_USER	system_users	786e213d-d2bd-4541-883f-73e3b51e7da0	\N	\N	2026-06-05 11:36:52.779543
05491a7b-e707-4df4-a294-ca4287cb1d36	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	786e213d-d2bd-4541-883f-73e3b51e7da0	\N	\N	2026-06-05 11:37:43.987245
936c1d91-b8f5-4503-99e6-ffed998b59b5	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-05 12:09:56.23534
8d1fb46b-ea35-4e99-b2f2-b8fa2409be5e	4756ff76-d053-442f-810f-361073e5a6e1	SCREEN_PATIENT	patients	bfb73641-679c-4161-8587-b48754d8ea9b	\N	\N	2026-06-05 12:16:53.3343
7043df5e-029b-449e-91c4-510f86247b6a	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-06 17:01:50.298608
b70a5249-25ae-4563-82ab-3975b17c2c96	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-06 17:06:05.599014
205f6ffe-e6df-464b-91b8-e281859f42d6	4756ff76-d053-442f-810f-361073e5a6e1	SCREEN_PATIENT	patients	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	\N	\N	2026-06-06 17:09:37.499819
7f931aec-6a0e-4694-bf3f-26646991b5a0	4756ff76-d053-442f-810f-361073e5a6e1	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-06 17:21:42.211264
66f995ca-b776-4974-a733-ee2753f50191	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	\N	\N	2026-06-06 17:22:19.599828
cfcf6c36-e2f9-4509-a9c9-2cf77358a6b9	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-09 07:50:06.660131
d3a3c90a-23f2-4046-a270-d973db89e5b9	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-09 07:55:41.359904
ad15fd5b-dcd5-4309-9d92-f737eae8ca11	4756ff76-d053-442f-810f-361073e5a6e1	SCREEN_PATIENT	patients	0ffb9432-cc2a-4814-aa7a-488551497730	\N	\N	2026-06-09 07:56:12.158997
cec38e32-e673-4654-9519-5404879e5e08	4756ff76-d053-442f-810f-361073e5a6e1	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-09 07:58:29.257027
9882d79d-185a-40aa-83fb-16931a5b7384	d40c82fe-c3f5-4105-be8a-b95b7308077f	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-09 08:11:42.475511
b030d530-3966-4622-9033-f97437997274	4756ff76-d053-442f-810f-361073e5a6e1	SCREEN_PATIENT	patients	e0f9a10b-4b32-49c7-bb9d-fb8df101610f	\N	\N	2026-06-09 08:12:14.26075
7a762a97-347c-47d8-ab6c-38ea1e82b2e9	4756ff76-d053-442f-810f-361073e5a6e1	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-09 08:13:43.467245
cbcda4bd-18a9-4bdf-b447-79d972560148	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	e0f9a10b-4b32-49c7-bb9d-fb8df101610f	\N	\N	2026-06-09 08:14:11.560459
a56b2d83-0fd9-400f-a01d-8f489667c662	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-09 08:43:11.603597
b43b0963-184a-4062-8125-d31f086c3946	4756ff76-d053-442f-810f-361073e5a6e1	SCREEN_PATIENT	patients	46bb83b4-0890-4763-b54e-1ec649d5062b	\N	\N	2026-06-09 08:52:03.406369
76c6ac07-a69d-4588-87da-ee4651913ae9	4756ff76-d053-442f-810f-361073e5a6e1	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-09 08:53:22.106483
71d9a530-d273-4829-8b78-ce262200d7fc	d40c82fe-c3f5-4105-be8a-b95b7308077f	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-09 08:56:09.608281
c4745bb4-31c2-49ad-b07f-56a50f3bde0f	4756ff76-d053-442f-810f-361073e5a6e1	SCREEN_PATIENT	patients	6b86e729-1dd2-4de7-8ec6-4382838cf376	\N	\N	2026-06-09 08:59:48.506743
eebd4e21-3bda-4e41-be47-3f132733edd1	4756ff76-d053-442f-810f-361073e5a6e1	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-09 09:01:20.815667
395e50a4-4198-43f1-b09c-fe548660539a	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-09 09:29:48.710055
e08f2ce2-0b0d-40a2-b9b1-eec171a97513	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-09 09:55:44.813241
75209d14-991c-45f7-af8a-21f5f10d3329	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 08:59:43.415742
b2ef0274-39d4-4e93-8344-c3992d0fc2ea	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 09:02:49.819446
f599adc9-9163-469b-a8e6-39ee611dc84b	97375343-31d6-4817-8ac0-7542d847fc49	SCREEN_PATIENT	patients	9034f0b1-8d54-4d8a-83c3-f723077062b2	\N	\N	2026-06-10 09:05:08.707109
432d47f9-062c-4206-8c93-fe94c5bce6f2	97375343-31d6-4817-8ac0-7542d847fc49	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 09:07:51.008551
47d937c0-b9e5-4dba-9dbe-8b3a53d38dbe	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 10:05:23.344134
c25d78ed-3fa2-4b88-9551-9c881890a36e	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 10:23:19.136919
dce15c04-b78b-4b8c-9e9f-27df2f7a3bc4	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	9034f0b1-8d54-4d8a-83c3-f723077062b2	\N	\N	2026-06-10 10:24:20.55206
37c97e9a-3fce-4e85-acdf-61c626660a86	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 10:50:38.116134
ad36f5fd-c3e7-43a6-9251-ee48c86be135	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 14:23:34.078988
a9d0baa1-80c8-4cfb-b4bc-d8e6b8844efe	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	b23bc14f-1785-4679-bdbe-81f0c1b779f2	\N	\N	2026-06-10 14:26:04.079254
637d9c26-46b3-4c20-bfb0-3ce741cb2c14	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	b23bc14f-1785-4679-bdbe-81f0c1b779f2	\N	\N	2026-06-10 14:28:03.876577
354b2047-d01b-41f5-9ae7-b9e82588061d	b23bc14f-1785-4679-bdbe-81f0c1b779f2	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 14:33:34.674958
370b5c94-da01-436c-878b-2eb5ac8ba4b6	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 15:18:57.669402
3ce3472b-416d-40c7-b4a5-96d83c106ac0	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 15:48:59.059613
adc39248-9498-47b5-bd9f-2dc69e45d7c6	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 16:42:38.288248
3c2f764d-2769-4e65-a74d-ba9e2688f032	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-10 16:46:20.631118
64e1d0ce-633e-48db-80c4-d3c810803866	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 17:15:27.619085
95c9993f-d7a1-4d47-8fb4-74c9d2c0f2a1	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 17:15:45.117351
020b1c37-8dff-4814-b4c5-6bc6da03c115	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 17:16:09.134508
9fc0865f-90f4-486d-b4b8-6810a305381d	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 17:16:10.538171
a247da64-9464-434c-a1f3-aae1a945658c	97375343-31d6-4817-8ac0-7542d847fc49	SCREEN_PATIENT	patients	49ca4358-c32e-44ba-bc52-92d731ea8f5b	\N	\N	2026-06-10 17:16:13.121911
da8ed9d6-ffac-4a89-9419-4dd82120cfa5	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	49ca4358-c32e-44ba-bc52-92d731ea8f5b	\N	\N	2026-06-10 17:16:16.218495
ea857cd0-7f0c-46ac-b555-111b522acfa4	97375343-31d6-4817-8ac0-7542d847fc49	SCREEN_PATIENT	patients	35340b74-bb21-41dd-95e1-c27d7531a959	\N	\N	2026-06-10 17:16:24.118603
be376b88-ab6c-4f5f-9e19-352b8c447b0b	97375343-31d6-4817-8ac0-7542d847fc49	SCREEN_PATIENT	patients	6b2a9c4a-9845-406f-bf96-5e92606e3309	\N	\N	2026-06-10 17:16:25.526856
82cc2a92-07cc-4083-827d-606b75c6600b	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	6b2a9c4a-9845-406f-bf96-5e92606e3309	\N	\N	2026-06-10 17:16:27.429481
b2ab45e7-47cc-4b12-8c00-990b5cdd89e4	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 18:44:37.639743
e45e6227-caae-49a5-9007-5fae1f786d59	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 19:23:51.741976
06a8b688-8408-4798-8d09-c41e317d6a12	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 19:25:03.048774
0064d2d3-8677-4e6a-8b17-ee3b3f7f2fca	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 19:25:15.550288
467ab54d-53bb-4807-b398-d36d1a1d2de8	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 19:30:50.635019
d133c64e-0614-494e-a09f-eabfbfaea60b	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 19:37:51.654104
5159db55-6519-45ef-af1f-1a28462260b9	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 19:42:14.247967
be946947-0473-4254-ab88-3472382ca0ae	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 19:42:39.952184
8017f9ed-4352-43e8-a8c6-8a121944d604	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 19:44:04.846879
528792d2-4fa3-42ef-8643-11bcffe4c15a	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 19:46:30.53356
23e8b32b-2fd8-4453-9904-2145d6f68ba4	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 20:08:31.212505
981d35ae-e380-4920-ab4d-6b7b6220f089	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 20:09:58.725901
5f711c09-53d7-4f5e-8329-238673d98147	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 20:57:24.038651
da534fa9-82bc-4b6d-b695-c3c09e792356	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 20:57:50.244852
3c1e82f1-872c-4d36-b2b5-1238bfde0e91	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-10 20:58:06.644676
c44dbe79-f31c-472b-ac05-fe3ad994d9e7	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 20:59:35.343339
9f32a289-0429-4eeb-92f4-b827f181ede4	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-10 20:59:36.638188
0adb7c2b-aade-41fd-ab97-951b394396e5	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-10 20:59:38.635461
2ea4a493-20c8-489e-89fe-5725d9025f24	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-10 20:59:40.540855
0e87ff6c-0a2e-4f33-9ba7-695cc9370b3f	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-10 22:08:59.132161
159d735e-3ae3-46db-9ffb-5db2eaa75291	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 08:44:29.44926
1ac14806-fa45-47e1-9594-49a51d523ef5	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 09:44:18.473869
7126c3a0-94bd-48a9-9322-076d4dcef15a	3b8dafc7-ab88-49a0-8699-686d991df1ff	GENERATE_TRACING_TASK	tracing_tasks	8e30eb4b-0488-4f86-921a-592e9aa287dd	\N	\N	2026-06-11 09:52:25.678319
977e1f2b-08f4-498d-aa75-dd53463b515e	3b8dafc7-ab88-49a0-8699-686d991df1ff	UPDATE_TRACING_STATUS	tracing_tasks	8e30eb4b-0488-4f86-921a-592e9aa287dd	\N	\N	2026-06-11 09:53:00.277232
e05493b9-63ae-47f5-b5ce-03306add79e9	3b8dafc7-ab88-49a0-8699-686d991df1ff	UPDATE_TRACING_STATUS	tracing_tasks	8e30eb4b-0488-4f86-921a-592e9aa287dd	\N	\N	2026-06-11 09:53:01.969835
da25c5f4-22ea-4144-8553-7aedbf706cbc	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESOLVE_TRACING_TASK	tracing_tasks	8e30eb4b-0488-4f86-921a-592e9aa287dd	\N	\N	2026-06-11 09:53:34.875125
403341a8-4d2e-4277-9580-079aa04fb616	3b8dafc7-ab88-49a0-8699-686d991df1ff	GENERATE_TRACING_TASK	tracing_tasks	cbc0273b-7972-4686-a04d-3065b16a6080	\N	\N	2026-06-11 09:54:19.785752
6988deeb-29ae-4f0f-af66-7731e3d4c774	3b8dafc7-ab88-49a0-8699-686d991df1ff	ESCALATE_TRACING_TASK	tracing_tasks	cbc0273b-7972-4686-a04d-3065b16a6080	\N	\N	2026-06-11 09:54:20.972349
3933aaa8-3b85-4e79-aed6-365c5db6abe6	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-11 16:49:05.747607
77df5e3e-6b84-4fb2-8360-b31806627a33	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-11 16:50:16.247441
2cac0f3a-5acb-48fd-9519-78166f497ee3	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-11 16:51:47.85176
b7136520-6c4d-4387-bc98-9456102dc8e8	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-11 16:55:35.645956
db05af11-eaf5-46a4-b412-e9a78e2b573d	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 17:17:03.756085
dc68b1f3-cbda-4209-9c43-a16066b5185b	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 18:35:38.862098
c10bde5e-b339-4f9a-9fcf-6e48a2489b4b	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 18:35:38.957698
319dbfab-664d-42b3-b10b-1c156ffa1a2f	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 18:36:26.76492
e2f86bd3-61c1-4d38-866c-312c54446389	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-11 18:57:41.968238
405a7889-0d16-4246-8e6d-5dd5f91b975f	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-11 18:57:43.356396
71c491b4-d580-4f03-9cf3-3c6df5daf859	97375343-31d6-4817-8ac0-7542d847fc49	SCREEN_PATIENT	patients	dce2bd61-cee2-4aac-a559-d537bbd6d175	\N	\N	2026-06-11 18:57:44.455561
64799aca-a05a-4f9f-aac2-460aacefaf13	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	dce2bd61-cee2-4aac-a559-d537bbd6d175	\N	\N	2026-06-11 18:57:46.856282
b81e3112-9507-4c90-a0bc-68ed7a306694	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 20:05:32.992638
1a5bb1ac-6576-440f-96e1-43684e33b92b	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 20:20:10.810006
4edc0160-4760-4848-8cf4-6a8920c28d2c	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 20:20:23.290858
4891d01e-57ad-4425-a78a-62398f5df6b3	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 20:20:32.095776
05711da5-d19b-4dcd-a2a4-e7d6e4ddd067	3b8dafc7-ab88-49a0-8699-686d991df1ff	GENERATE_TRACING_TASK	tracing_tasks	36eab4a7-2490-43f0-ac4d-21add5bfc4ce	\N	\N	2026-06-11 20:20:50.091102
54e5bf8f-a3b2-4264-b1b6-b62e8303194c	3b8dafc7-ab88-49a0-8699-686d991df1ff	GENERATE_TRACING_TASK	tracing_tasks	80d28047-e5c5-411d-a415-53d664f05001	\N	\N	2026-06-11 20:20:51.490971
883bf715-fbbe-4bd2-9714-8a5aff59fbe0	3b8dafc7-ab88-49a0-8699-686d991df1ff	GENERATE_TRACING_TASK	tracing_tasks	966687ed-aa4e-4aa7-acd6-7d88b6b9bf98	\N	\N	2026-06-11 20:20:52.392399
82cb25f9-08a1-4f28-8acb-c8b3d4b59bdb	3b8dafc7-ab88-49a0-8699-686d991df1ff	UPDATE_TRACING_STATUS	tracing_tasks	80d28047-e5c5-411d-a415-53d664f05001	\N	\N	2026-06-11 20:21:05.391028
a3c878ea-856f-4a0d-b5f1-97d4b82f8b75	3b8dafc7-ab88-49a0-8699-686d991df1ff	UPDATE_TRACING_STATUS	tracing_tasks	966687ed-aa4e-4aa7-acd6-7d88b6b9bf98	\N	\N	2026-06-11 20:21:06.987227
056a638c-85d3-443a-a726-11258b0e3f85	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-11 20:50:02.160804
ce14e6a3-34d5-4493-ab75-1baccd20729e	\N	LOGIN	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-11 20:53:24.456796
e80b458e-3381-42bc-9ffe-c23c58aa6365	760a3d01-20fa-4f17-8889-36305ef433aa	CHANGE_PASSWORD	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-11 20:54:51.259186
cb3929fd-67a6-4d2f-873c-53f6d3a5ee3d	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-11 21:00:23.661824
c45212a6-5c42-4d06-99a3-467c801dc4bb	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-11 21:06:23.363253
a0367f0c-0198-469d-a2fd-c08b6d30a574	97375343-31d6-4817-8ac0-7542d847fc49	RECORD_VISIT	home_visits	dd57587e-1441-4757-8189-05fae7c0b914	\N	\N	2026-06-11 21:09:59.355658
44780ab1-4f80-41d3-a0fe-4bae3bed0e41	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-11 21:29:39.15637
5205e240-9286-4c75-9084-894f830a6e32	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-11 21:31:18.563343
00dcd118-37c0-4ac1-a555-3606530dc68d	97375343-31d6-4817-8ac0-7542d847fc49	RECORD_VISIT	home_visits	5f6cf3aa-c795-4f36-8aed-b44730640617	\N	\N	2026-06-11 21:42:30.158662
f213e14e-7839-44ec-91c5-bd31648d449c	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 21:57:16.957236
4e126c0d-4e8f-4f85-b9f7-9921560cec83	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-11 22:01:24.25616
ddc30d6e-fa6d-4b1d-b383-166cdd1af1aa	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-11 22:01:48.764795
e581afc3-4e64-44db-92b5-74aded2835de	3b8dafc7-ab88-49a0-8699-686d991df1ff	UNLOCK_USER	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-11 22:07:55.925349
3a051714-7ee3-41fb-ad15-bf40c0ecd2b9	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-11 22:08:10.055222
0b72d3d3-a120-4e4c-b4f5-346d0042812c	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-11 22:08:11.466972
aba69d96-7846-4b3a-8755-25c6b82e4b11	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-11 22:33:06.566734
0049cc8b-eb7c-4a79-b081-66255e9cb1a2	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	CHANGE_PASSWORD	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-11 22:33:40.560612
0d5cbeaa-0135-48d4-9f2d-a6b37340036a	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-11 23:13:05.118287
869a18ee-c7de-4692-86b0-66805ffa96d3	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-12 18:51:22.361868
61e02519-3ec4-44c8-bf62-c6f0cb96ff34	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-14 12:24:38.143269
a7031193-687b-47b8-aed4-6b70fb47269f	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-14 12:24:56.147844
f8b81b3f-2808-481d-96f5-38c52801711d	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-14 12:28:32.454541
7c9122a4-f974-4526-9869-490bfb0e8274	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-14 12:32:09.246955
06a69c12-1957-4bde-8640-690d1da6fe96	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-14 12:43:18.652113
162f9fff-8251-4e37-b759-6fb136c07e33	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-14 21:32:21.209051
0331ca9c-9950-4994-8eb7-cbcd80ae3b8f	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-14 21:34:28.524735
0f3cca4a-571d-41ba-a8d3-9cbc420e2b53	97375343-31d6-4817-8ac0-7542d847fc49	SCREEN_PATIENT	patients	9c3bfdce-a0a2-4d3c-8c00-41988527b119	\N	\N	2026-06-14 21:35:07.610738
c9db567c-2ba4-4fc7-8bd2-692746433ecd	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-14 21:40:39.714474
d92e533e-f869-4656-91c5-926aec4310da	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	9c3bfdce-a0a2-4d3c-8c00-41988527b119	\N	\N	2026-06-14 21:41:03.510166
ad8f5d70-5f72-487a-82a1-e4b0c6c0b080	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 08:40:13.842184
f175876e-026f-439e-ab7f-bdf0ff9ed67f	97375343-31d6-4817-8ac0-7542d847fc49	SCREEN_PATIENT	patients	8009c775-9249-45bf-8f23-5270418f75d1	\N	\N	2026-06-15 08:41:18.337085
523dbbaf-fd11-477c-926f-88aef0a74cf4	97375343-31d6-4817-8ac0-7542d847fc49	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-15 08:42:13.936011
92a3579a-dc60-4d7e-8174-816b056afba5	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	8009c775-9249-45bf-8f23-5270418f75d1	\N	\N	2026-06-15 08:43:55.141424
a7ebfa9d-c568-4d8a-94df-c21a172c542e	d40c82fe-c3f5-4105-be8a-b95b7308077f	LOGIN	system_users	3870dcd3-2a10-41e7-893e-c3dc2c6c88df	\N	\N	2026-06-15 09:00:43.541786
c00a7ea2-a45f-4fcc-8d3c-7afcb0190afa	3870dcd3-2a10-41e7-893e-c3dc2c6c88df	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 09:05:48.742076
a7e29003-9ef7-4443-b8ab-4ff4aaf67996	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 09:36:59.888722
f2631879-dcea-41b1-8cf2-7657def806fc	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-15 10:32:15.226952
1ebaf9a8-f29b-4681-ac1c-a957120d7ddd	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-15 11:24:47.019789
f14825b1-2603-4ff2-b8c4-188587b5942d	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-15 13:24:05.992812
f13fe90e-7432-4e97-ba23-73561e4d1cb5	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-15 18:37:33.361816
735d4b50-0b01-4154-b2b5-4bdf777ecc9f	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-15 18:40:50.635382
c70637fe-be7d-4775-8768-0381dabf0ba6	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-15 19:26:49.419214
8107654a-6043-444e-a74c-2cdcdacabf35	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-15 19:49:30.821487
5b99c652-7ba9-4ef1-9e10-5a41797fb091	3b8dafc7-ab88-49a0-8699-686d991df1ff	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 19:52:20.633325
e6dcb244-1c33-423b-bd8f-92ca825ad632	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-15 20:01:28.913144
dfbcd9fa-ab0a-4e78-8bf6-47767d1cad80	3b8dafc7-ab88-49a0-8699-686d991df1ff	GENERATE_TRACING_TASK	tracing_tasks	65aaf941-1d05-4983-a824-741fc6ff01f9	\N	\N	2026-06-15 20:03:00.633355
90d8d1e2-bd52-444f-b524-c8237b8e57fd	3b8dafc7-ab88-49a0-8699-686d991df1ff	GENERATE_TRACING_TASK	tracing_tasks	e571c328-4d13-42d4-abd7-1bdbe1f06417	\N	\N	2026-06-15 20:15:57.615809
96b24a78-cd09-44db-8cf3-6a2063ebef95	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 20:22:08.438895
d0ec1837-84ae-4225-85cc-f5bf3874e5d1	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 20:23:24.524788
034fd28f-77a4-42af-b2bc-c52553ae704f	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 20:32:40.924294
0ea40387-7f25-4cb5-aa28-27e1988a8da9	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-15 21:03:30.5548
c0916b54-482b-4c37-9112-6c33d25a7d3e	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-15 21:04:25.654483
5daa0d2e-1d9a-4ef2-8e19-6efb90396dff	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-16 10:40:56.665064
0c6c553c-babe-4cd6-b237-2bdf163214fe	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-16 10:41:56.972802
b6ec986c-89fa-4a75-bc1f-ddf20de62b93	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-16 10:43:08.170216
399f6752-348a-43ce-8095-cc8d84063017	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-16 10:50:28.970368
89050fc7-f4bc-4875-934e-2914da6847a6	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-16 10:52:25.465025
cd07e892-0ecf-4000-9c88-f0c5e2bd01f3	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-16 10:53:44.070657
d5468fb3-2e88-47ac-80d5-84cd5e9e16e7	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-16 13:35:21.04407
1ba802de-b9bc-4b2f-8dc6-b0192b25fe63	3b8dafc7-ab88-49a0-8699-686d991df1ff	UNLOCK_USER	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-16 18:26:56.706899
94159b27-bda6-4392-868f-37185ddbc79b	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-16 18:27:06.212298
091a1831-598d-42e2-b82b-95bbb0df2ae5	\N	LOGIN	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-16 18:28:42.326365
3a87da78-10f1-44b4-a176-638f40be2a2c	760a3d01-20fa-4f17-8889-36305ef433aa	CHANGE_PASSWORD	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-16 18:29:57.307021
de65a8c5-c617-43e9-bd84-1fd1cec82014	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-18 08:49:55.525462
8a540659-1145-4dac-b045-d423b46a4032	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-18 10:19:07.228843
863b0301-dc41-44f8-b3e7-27345572d8dc	97375343-31d6-4817-8ac0-7542d847fc49	RECORD_VISIT	home_visits	aedd50e5-2a44-4a7a-abd4-534e8b7d93ff	\N	\N	2026-06-18 10:42:25.63814
32b368d1-3c65-4ff5-b337-67f94aa6f64c	97375343-31d6-4817-8ac0-7542d847fc49	RECORD_VISIT	home_visits	9b4b2b93-dabc-47ed-a31f-8389afc1d42c	\N	\N	2026-06-18 10:44:30.445513
10f729d2-cf86-476e-8de2-7398235f642b	97375343-31d6-4817-8ac0-7542d847fc49	RECORD_VISIT	home_visits	8debce31-4d28-4f70-afec-0d4896e96511	\N	\N	2026-06-18 10:46:05.950978
5055291a-f130-450f-ac3e-e366a71b1048	97375343-31d6-4817-8ac0-7542d847fc49	RECORD_VISIT	home_visits	68e6d8b0-ba2a-4b19-93e0-30bd7bad60b6	\N	\N	2026-06-18 10:46:15.057087
570293c6-1e44-4f71-b57f-3267440603e2	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-18 10:50:12.641495
29774d1d-0919-4f85-a3f4-c79a3fd946a1	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-18 10:53:47.844118
f93c33ca-51f9-47a9-9d3c-f41429d52af1	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-18 11:03:22.153794
c990ae49-5547-4953-97a3-6cd5d0ac89a0	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-18 11:06:03.248558
a0b230ce-7d60-4600-9c8f-2e27050c0ec1	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-18 19:56:11.306092
b1319686-bd51-47b1-a467-b6063f8a3449	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-18 19:58:59.302275
dd4a00a0-bb76-4062-9ec4-f184f4be5278	3b8dafc7-ab88-49a0-8699-686d991df1ff	RESET_PASSWORD	system_users	74e6bcd2-d381-4bc4-988f-99e7948929e1	\N	\N	2026-06-18 20:02:36.713355
e66ffe76-962b-430e-b1a1-65564b5f35e1	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-18 20:03:16.604501
4b1b2215-db55-4d81-8eb9-b86d5a7641fb	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-18 20:09:38.713289
8e75f505-5c05-4709-bc29-1fd61414ba3d	3b8dafc7-ab88-49a0-8699-686d991df1ff	CREATE_USER	system_users	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N	\N	2026-06-18 20:15:20.208338
d1905d55-98b1-4000-9e14-80747d6e9c95	3b8dafc7-ab88-49a0-8699-686d991df1ff	CREATE_USER	system_users	ba8e2562-bcff-4027-8210-84caec343ab6	\N	\N	2026-06-18 20:21:20.52565
d5a9c7fd-d6f7-47f7-b5a3-bd0c61b4932f	\N	LOGIN	system_users	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N	\N	2026-06-18 20:25:58.510099
e485de0c-548a-4e55-a0dd-6ef2acd6ba2b	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	CHANGE_PASSWORD	system_users	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N	\N	2026-06-18 20:26:55.706909
b3ab2b1e-f0a8-490e-84a7-eb9f5a5af9e9	\N	LOGIN	system_users	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N	\N	2026-06-18 20:47:58.109806
ff918575-37d3-4809-beac-c92827b73d2a	\N	LOGIN	system_users	ba8e2562-bcff-4027-8210-84caec343ab6	\N	\N	2026-06-18 20:49:04.20771
8705b768-6dfd-46cd-9429-45a4be821192	ba8e2562-bcff-4027-8210-84caec343ab6	CHANGE_PASSWORD	system_users	ba8e2562-bcff-4027-8210-84caec343ab6	\N	\N	2026-06-18 20:49:52.602043
967ef121-72af-4200-98af-946a6bea1d39	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-18 20:57:12.306232
0626d692-7861-4d07-a4dd-41d8bebe61e3	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-18 21:05:28.409514
f3bf5b4f-ac07-4c5b-b957-4f5d14f1eb20	\N	LOGIN	system_users	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N	\N	2026-06-18 21:09:06.116296
d14376cf-e867-4306-8496-f16e9c30c7de	4756ff76-d053-442f-810f-361073e5a6e1	RESOLVE_TRACING_TASK	tracing_tasks	80d28047-e5c5-411d-a415-53d664f05001	\N	\N	2026-06-18 21:19:09.005505
6746b99d-f529-42cb-8af7-3f8d6a471523	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-18 21:26:43.901142
b64ed7e7-a741-4741-8100-120ca344a987	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-18 23:05:11.111126
7e41923b-4ad6-4644-84c3-4d6c17ed0fa4	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-18 23:10:42.710905
12f54270-20ed-4212-913f-e6fced12d72d	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-18 23:12:16.718361
fbc4bda2-bc47-45d2-8d16-934f6821d851	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-18 23:14:33.314496
febf8904-c06d-4dd4-bdea-efa1c2bf0585	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-18 23:15:06.519
ecfb8656-40c4-45c6-a2aa-fefbe255aabe	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-18 23:22:26.725925
23edecd3-3cbe-4505-ac4a-e4ecd59329fa	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-18 23:24:49.009377
448233ff-05ea-4285-89a4-149d79a473e2	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-19 04:48:02.425843
1d7d1b79-12e4-4c18-84c3-4c7ca28b121b	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-19 04:51:40.340339
203eca29-a1d6-467a-a654-ef19f0f68978	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-19 04:59:16.931051
147eeb3f-22f9-491a-adc8-0f56c08e9f41	4756ff76-d053-442f-810f-361073e5a6e1	RECORD_VISIT	home_visits	ab996a7f-f8b0-4925-825b-78f3353eed2e	\N	\N	2026-06-19 05:00:19.826592
a51e1e9c-2ead-4496-a289-193bc55d8223	\N	LOGIN	system_users	760a3d01-20fa-4f17-8889-36305ef433aa	\N	\N	2026-06-19 05:33:07.333811
d3c21bfe-114d-43cf-b02c-c089cb5f88df	\N	LOGIN	system_users	d40c82fe-c3f5-4105-be8a-b95b7308077f	\N	\N	2026-06-19 05:40:05.957548
5345e0bb-9e9e-4a8b-a5b3-273b59a40dc2	d40c82fe-c3f5-4105-be8a-b95b7308077f	CONFIRM_PATIENT	patients	bfb73641-679c-4161-8587-b48754d8ea9b	\N	\N	2026-06-19 05:41:33.768492
8eb46a7a-1c7a-4948-b424-1eea2cb5c877	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-19 07:17:14.836666
01fea22c-005c-4b97-a886-fbc66d3ea04f	97375343-31d6-4817-8ac0-7542d847fc49	ESCALATE_TRACING_TASK	tracing_tasks	966687ed-aa4e-4aa7-acd6-7d88b6b9bf98	\N	\N	2026-06-19 07:17:55.336745
bd79776a-33c0-487b-a5bf-efbcd9ff7b85	97375343-31d6-4817-8ac0-7542d847fc49	ESCALATE_TRACING_TASK	tracing_tasks	966687ed-aa4e-4aa7-acd6-7d88b6b9bf98	\N	\N	2026-06-19 07:17:56.135068
03f6a27b-e7a3-4575-8334-fd68e514763f	97375343-31d6-4817-8ac0-7542d847fc49	ESCALATE_TRACING_TASK	tracing_tasks	966687ed-aa4e-4aa7-acd6-7d88b6b9bf98	\N	\N	2026-06-19 07:17:56.635422
a0be6e6a-7401-4873-90fc-6731358d31d5	\N	LOGIN	system_users	4756ff76-d053-442f-810f-361073e5a6e1	\N	\N	2026-06-19 07:18:53.038353
c9cb202b-b1b7-4734-b37f-31c33e0761e4	4756ff76-d053-442f-810f-361073e5a6e1	RESOLVE_TRACING_TASK	tracing_tasks	36eab4a7-2490-43f0-ac4d-21add5bfc4ce	\N	\N	2026-06-19 07:21:33.53675
2dc1f7b5-500f-4e64-a0e3-0d3b6a603f0a	\N	LOGIN	system_users	97375343-31d6-4817-8ac0-7542d847fc49	\N	\N	2026-06-19 07:27:08.946672
fbc2b9ff-319b-4595-9f03-c38995e814b9	97375343-31d6-4817-8ac0-7542d847fc49	RESOLVE_TRACING_TASK	tracing_tasks	e571c328-4d13-42d4-abd7-1bdbe1f06417	\N	\N	2026-06-19 07:28:23.485535
0d69f138-a905-4c8f-a8fa-9d73d9f0f99b	\N	LOGIN	system_users	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N	\N	2026-06-19 08:32:36.966135
f28d262a-a2ca-4ac1-a22d-3680b2c2a3bc	\N	LOGIN	system_users	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	\N	\N	2026-06-19 10:01:06.049025
a0d9162f-f31c-4145-a350-ca587ba35680	\N	LOGIN	system_users	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	\N	\N	2026-06-19 10:28:39.751744
2a57d829-2bfe-4c6c-a727-d77bf7a63044	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-19 10:36:27.749362
9d530371-9d2d-4eb3-897c-fc19ad5fe716	\N	LOGIN	system_users	3b8dafc7-ab88-49a0-8699-686d991df1ff	\N	\N	2026-06-19 23:52:18.231065
\.


--
-- Data for Name: chws; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.chws (id, user_id, facility_id, assigned_village, assigned_sector, employee_code, is_active, created_at) FROM stdin;
02a4d824-e859-45e5-9092-40dc69fa4c89	4756ff76-d053-442f-810f-361073e5a6e1	c40666ca-46c3-45d7-a3b1-e25faa8f0126	Kimisagara	Nyarugenge	CHW-001	t	2026-05-30 15:53:06.486049
1dd0bb68-cd37-4945-846c-8309613c438a	97375343-31d6-4817-8ac0-7542d847fc49	c40666ca-46c3-45d7-a3b1-e25faa8f0126	Gatsibo	Niboye	chw-001	t	2026-05-30 16:51:17.519714
\.


--
-- Data for Name: confirmation_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.confirmation_logs (id, patient_id, confirmation_method, response_time_seconds, window_open_time, window_close_time, is_within_window, created_at, plan_id, schedule_id, scheduled_date, confirmed_at, raw_sms_response, is_missed, ai_suspicion_flag, suspicion_reason) FROM stdin;
a099bb47-828b-4caf-922c-d2974c8a7c71	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-10 08:00:00	2026-06-10 09:00:00	f	2026-06-10 10:55:25.519863	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-10	\N	\N	t	f	\N
4766eab4-7d1c-416b-b501-0e3dad566994	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	98	2026-06-09 08:00:00	2026-06-09 09:00:00	t	2026-06-10 19:16:39.646972	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-09	2026-06-09 08:01:38	\N	f	f	\N
2f530e0c-a216-4756-8ee1-4c68d24109c2	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	106	2026-06-08 08:00:00	2026-06-08 09:00:00	t	2026-06-10 19:16:40.30887	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-08	2026-06-08 08:01:46	\N	f	f	\N
ad032a48-3b0b-4593-a739-d2125fe01af4	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	114	2026-06-07 08:00:00	2026-06-07 09:00:00	t	2026-06-10 19:16:41.194047	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-07	2026-06-07 08:01:54	\N	f	f	\N
898d28b7-f053-41fc-8c66-117cca470b7d	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	122	2026-06-06 08:00:00	2026-06-06 09:00:00	t	2026-06-10 19:16:41.51744	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-06	2026-06-06 08:02:02	\N	f	f	\N
af8a03d4-4397-4177-b0e6-af55ba12160b	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	130	2026-06-05 08:00:00	2026-06-05 09:00:00	t	2026-06-10 19:16:41.88686	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-05	2026-06-05 08:02:10	\N	f	f	\N
38a6dca9-d95c-43dc-b641-30afe814ee7d	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	90	2026-06-04 08:00:00	2026-06-04 09:00:00	t	2026-06-10 19:16:42.216044	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-04	2026-06-04 08:01:30	\N	f	f	\N
c6dc80fe-9e3b-4b10-badb-63b6b19d3a2a	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-03 08:00:00	2026-06-03 09:00:00	f	2026-06-10 19:16:43.106216	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-03	\N	\N	t	f	\N
37260f33-0505-4bfc-9801-9c2b46c64cbc	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	106	2026-06-02 08:00:00	2026-06-02 09:00:00	t	2026-06-10 19:16:43.986961	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-02	2026-06-02 08:01:46	\N	f	f	\N
4346cb71-b86d-46e3-90d7-175bf38e6c52	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	114	2026-06-01 08:00:00	2026-06-01 09:00:00	t	2026-06-10 19:16:44.339606	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-01	2026-06-01 08:01:54	\N	f	f	\N
27698441-2007-4338-83aa-f8590ae2c3a1	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	122	2026-05-31 08:00:00	2026-05-31 09:00:00	t	2026-06-10 19:16:44.718782	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-31	2026-05-31 08:02:02	\N	f	f	\N
54a28d0c-10f8-4710-8ffa-0c591504b1b6	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	130	2026-05-30 08:00:00	2026-05-30 09:00:00	t	2026-06-10 19:16:45.601207	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-30	2026-05-30 08:02:10	\N	f	f	\N
1b0357ba-3c3a-4395-85b7-3d8d0fc95143	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	90	2026-05-29 08:00:00	2026-05-29 09:00:00	t	2026-06-10 19:16:45.949619	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-29	2026-05-29 08:01:30	\N	f	f	\N
dea22c2d-01b6-4785-bd50-7eb5084f6474	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	98	2026-05-28 08:00:00	2026-05-28 09:00:00	t	2026-06-10 19:16:46.296872	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-28	2026-05-28 08:01:38	\N	f	f	\N
fdddbb63-7f84-4646-9813-3e36089ae62e	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	106	2026-05-27 08:00:00	2026-05-27 09:00:00	t	2026-06-10 19:16:46.632495	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-27	2026-05-27 08:01:46	\N	f	f	\N
bb6e836f-2093-446b-9a42-8581cdafe9a5	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	114	2026-05-26 08:00:00	2026-05-26 09:00:00	t	2026-06-10 19:16:46.956588	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-26	2026-05-26 08:01:54	\N	f	f	\N
9710ad36-ac19-4c91-a15e-2b1a483ba17e	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	122	2026-05-25 08:00:00	2026-05-25 09:00:00	t	2026-06-10 19:16:47.286539	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-25	2026-05-25 08:02:02	\N	f	f	\N
29095ff7-bee7-43d3-8c6d-94d4053f5c66	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	130	2026-05-24 08:00:00	2026-05-24 09:00:00	t	2026-06-10 19:16:47.612006	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-24	2026-05-24 08:02:10	\N	f	f	\N
ef8c1acc-f19c-432b-963b-a89a77d54c32	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	90	2026-05-23 08:00:00	2026-05-23 09:00:00	t	2026-06-10 19:16:47.938451	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-23	2026-05-23 08:01:30	\N	f	f	\N
77bc8ae1-37df-435f-aa10-1b34a6059815	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-05-22 08:00:00	2026-05-22 09:00:00	f	2026-06-10 19:16:48.278532	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-22	\N	\N	t	f	\N
dc9a3ee8-78cb-485f-b093-356543b1ea0b	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	106	2026-05-21 08:00:00	2026-05-21 09:00:00	t	2026-06-10 19:16:48.650534	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-21	2026-05-21 08:01:46	\N	f	f	\N
883662cf-2d70-4357-bd29-7f3a495596c2	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	114	2026-05-20 08:00:00	2026-05-20 09:00:00	t	2026-06-10 19:16:49.006447	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-20	2026-05-20 08:01:54	\N	f	f	\N
34807cc8-5a99-4a64-8bfe-8d75732992e7	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	122	2026-05-19 08:00:00	2026-05-19 09:00:00	t	2026-06-10 19:16:49.339295	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-19	2026-05-19 08:02:02	\N	f	f	\N
cb2aad92-92b4-41ae-81d9-10e2cc2b1959	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	130	2026-05-18 08:00:00	2026-05-18 09:00:00	t	2026-06-10 19:16:49.679397	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-18	2026-05-18 08:02:10	\N	f	f	\N
4b53c3d5-022e-47f8-8bb2-a0f12876ea88	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	90	2026-05-17 08:00:00	2026-05-17 09:00:00	t	2026-06-10 19:16:50.021316	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-17	2026-05-17 08:01:30	\N	f	f	\N
03ccd362-6df2-4318-83e0-4c1919e6e60c	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	98	2026-05-16 08:00:00	2026-05-16 09:00:00	t	2026-06-10 19:16:50.348176	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-16	2026-05-16 08:01:38	\N	f	f	\N
2b5de069-6ae8-4088-96e1-3720b11cba1d	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	106	2026-05-15 08:00:00	2026-05-15 09:00:00	t	2026-06-10 19:16:50.675751	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-15	2026-05-15 08:01:46	\N	f	f	\N
79c5e53f-d83f-4cb9-96c8-f1726c0431a4	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	114	2026-05-14 08:00:00	2026-05-14 09:00:00	t	2026-06-10 19:16:51.005284	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-14	2026-05-14 08:01:54	\N	f	f	\N
9f656df5-a852-4811-ae29-af1dc7823c45	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	122	2026-05-13 08:00:00	2026-05-13 09:00:00	t	2026-06-10 19:16:51.335416	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-13	2026-05-13 08:02:02	\N	f	f	\N
c788b2fd-395c-48c2-8427-f36807f98460	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	130	2026-05-12 08:00:00	2026-05-12 09:00:00	t	2026-06-10 19:16:51.668328	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-12	2026-05-12 08:02:10	\N	f	f	\N
2f3c727e-ab3a-4ade-9e57-8a9ea51ced1e	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	90	2026-05-11 08:00:00	2026-05-11 09:00:00	t	2026-06-10 19:16:52.00408	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-05-11	2026-05-11 08:01:30	\N	f	f	\N
13bf3c44-f86a-48a2-8b6e-d2726e72e2b6	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	82	2026-06-09 07:30:00	2026-06-09 08:30:00	t	2026-06-10 19:16:53.017244	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-09	2026-06-09 07:31:22	\N	f	f	\N
f26c5dab-5b98-4849-9a4b-eed6c2a12ccd	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-08 07:30:00	2026-06-08 08:30:00	f	2026-06-10 19:16:53.889441	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-08	\N	\N	t	f	\N
212ae23e-a6f0-4355-96ab-f1d42a7fd9ae	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	106	2026-06-07 07:30:00	2026-06-07 08:30:00	t	2026-06-10 19:16:54.22402	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-07	2026-06-07 07:31:46	\N	f	f	\N
ceb7d419-9e5a-4d60-8aea-913f5e881307	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	118	2026-06-06 07:30:00	2026-06-06 08:30:00	t	2026-06-10 19:16:54.55757	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-06	2026-06-06 07:31:58	\N	f	f	\N
ac0ced2c-1c4b-4bad-a4e7-b7fd8f04c109	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-05 07:30:00	2026-06-05 08:30:00	f	2026-06-10 19:16:54.880725	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-05	\N	\N	t	f	\N
65c251b4-78e8-4053-86a1-603d8c65a61a	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	70	2026-06-04 07:30:00	2026-06-04 08:30:00	t	2026-06-10 19:16:55.205581	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-04	2026-06-04 07:31:10	\N	f	f	\N
2dc43cbb-0f39-4fbf-b4b3-fc6e2987ee0f	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	82	2026-06-03 07:30:00	2026-06-03 08:30:00	t	2026-06-10 19:16:55.543358	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-03	2026-06-03 07:31:22	\N	f	f	\N
a6568da6-966d-4a9f-93ad-fc0913a5518a	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	94	2026-06-02 07:30:00	2026-06-02 08:30:00	t	2026-06-10 19:16:55.874245	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-02	2026-06-02 07:31:34	\N	f	f	\N
214a65cc-3026-4e39-a9d2-5b01ef7dbb18	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-01 07:30:00	2026-06-01 08:30:00	f	2026-06-10 19:16:56.19707	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-01	\N	\N	t	f	\N
3c0368cd-8bb5-46dd-974a-66efccdad6d4	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	118	2026-05-31 07:30:00	2026-05-31 08:30:00	t	2026-06-10 19:16:56.541648	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-31	2026-05-31 07:31:58	\N	f	f	\N
b68acbe7-7b68-4ad5-a85e-1cfc2ffa88fb	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	130	2026-05-30 07:30:00	2026-05-30 08:30:00	t	2026-06-10 19:16:56.872773	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-30	2026-05-30 07:32:10	\N	f	f	\N
1012b9f0-8fa2-4f59-9f6d-08e099816cfd	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	70	2026-05-29 07:30:00	2026-05-29 08:30:00	t	2026-06-10 19:16:57.196315	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-29	2026-05-29 07:31:10	\N	f	f	\N
678ba322-b15c-496c-9f2d-41bd17dec45f	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-05-28 07:30:00	2026-05-28 08:30:00	f	2026-06-10 19:16:57.521524	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-28	\N	\N	t	f	\N
f39875d6-fffc-420d-a2dd-6133fa5bdccd	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	94	2026-05-27 07:30:00	2026-05-27 08:30:00	t	2026-06-10 19:16:58.370066	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-27	2026-05-27 07:31:34	\N	f	f	\N
fef7ca09-2e8b-4143-8949-f5ee078c178d	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	106	2026-05-26 07:30:00	2026-05-26 08:30:00	t	2026-06-10 19:16:58.698551	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-26	2026-05-26 07:31:46	\N	f	f	\N
e2d28bd2-8283-4e84-988d-4e914db14100	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	118	2026-05-25 07:30:00	2026-05-25 08:30:00	t	2026-06-10 19:16:59.057173	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-25	2026-05-25 07:31:58	\N	f	f	\N
727a27ae-e165-44a9-8a6a-829b58c0382b	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	130	2026-05-24 07:30:00	2026-05-24 08:30:00	t	2026-06-10 19:16:59.397913	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-24	2026-05-24 07:32:10	\N	f	f	\N
114a8cbe-fece-4250-99a6-93d500dd3ea9	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-05-23 07:30:00	2026-05-23 08:30:00	f	2026-06-10 19:16:59.724251	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-23	\N	\N	t	f	\N
a01e1151-ac38-404e-9802-dc399b690b28	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	82	2026-05-22 07:30:00	2026-05-22 08:30:00	t	2026-06-10 19:17:00.063613	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-22	2026-05-22 07:31:22	\N	f	f	\N
a6af1e02-11e4-41af-aed2-17311dbfd1b1	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	94	2026-05-21 07:30:00	2026-05-21 08:30:00	t	2026-06-10 19:17:00.400678	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-21	2026-05-21 07:31:34	\N	f	f	\N
4594648d-cfe4-47f3-91bc-dca9e95880ba	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	106	2026-05-20 07:30:00	2026-05-20 08:30:00	t	2026-06-10 19:17:00.742898	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-20	2026-05-20 07:31:46	\N	f	f	\N
51ee2640-c266-4d80-87d6-45ef11df5c03	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-05-19 07:30:00	2026-05-19 08:30:00	f	2026-06-10 19:17:01.066563	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-19	\N	\N	t	f	\N
3cca3b6c-811f-40c1-a5fe-94c137a25513	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	130	2026-05-18 07:30:00	2026-05-18 08:30:00	t	2026-06-10 19:17:01.40438	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-18	2026-05-18 07:32:10	\N	f	f	\N
0b1df6ad-d16f-4a55-938b-dfe1f19aa01e	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	70	2026-05-17 07:30:00	2026-05-17 08:30:00	t	2026-06-10 19:17:02.337428	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-17	2026-05-17 07:31:10	\N	f	f	\N
646fbcf4-078b-496b-9fa1-809f14a995d8	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	82	2026-05-16 07:30:00	2026-05-16 08:30:00	t	2026-06-10 19:17:02.687906	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-16	2026-05-16 07:31:22	\N	f	f	\N
0c5e3fae-e724-470a-a53d-a093e64e5fdc	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-05-15 07:30:00	2026-05-15 08:30:00	f	2026-06-10 19:17:03.023248	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-15	\N	\N	t	f	\N
9d231da8-e295-4632-9a3b-a36b59415696	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	106	2026-05-14 07:30:00	2026-05-14 08:30:00	t	2026-06-10 19:17:03.941945	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-14	2026-05-14 07:31:46	\N	f	f	\N
be12ea8b-da43-4216-9c99-9c284741ee92	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	118	2026-05-13 07:30:00	2026-05-13 08:30:00	t	2026-06-10 19:17:04.278577	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-13	2026-05-13 07:31:58	\N	f	f	\N
a8ee2fb4-9e1d-4335-8e9d-535be7d3c374	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-05-12 07:30:00	2026-05-12 08:30:00	f	2026-06-10 19:17:04.621212	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-12	\N	\N	t	f	\N
863939a2-d710-44de-b78a-79147215190f	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	70	2026-05-11 07:30:00	2026-05-11 08:30:00	t	2026-06-10 19:17:04.962007	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-05-11	2026-05-11 07:31:10	\N	f	f	\N
4b894833-8334-4b90-944e-08cd6d5eb29c	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-09 08:00:00	2026-06-09 09:00:00	f	2026-06-10 19:17:05.937366	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-09	\N	\N	t	f	\N
a0688a71-3bc8-4419-9b0f-ec931eb2a8a2	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-08 08:00:00	2026-06-08 09:00:00	f	2026-06-10 19:17:06.266506	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-08	\N	\N	t	f	\N
2244a34c-b9e3-4324-a16f-cbdd1535dfad	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-07 08:00:00	2026-06-07 09:00:00	f	2026-06-10 19:17:06.609217	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-07	\N	\N	t	f	\N
5ceefb80-2378-4d98-a5b0-c81671c32f0b	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-06 08:00:00	2026-06-06 09:00:00	f	2026-06-10 19:17:06.937421	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-06	\N	\N	t	f	\N
8b4742b6-eb19-4b3e-abf5-d12b9b31400c	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	28	2026-06-05 08:00:00	2026-06-05 09:00:00	t	2026-06-10 19:17:07.282977	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-05	2026-06-05 08:00:28	\N	f	f	\N
3c9dcab2-1206-462a-8d7b-cabcc7752b62	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-04 08:00:00	2026-06-04 09:00:00	f	2026-06-10 19:17:07.604528	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-04	\N	\N	t	f	\N
ca55e9f0-91d3-4c65-be27-16af53f2b10e	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	18	2026-06-10 08:00:00	2026-06-10 09:00:00	t	2026-06-10 14:31:53.571734	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-10	2026-06-10 08:00:18	\N	f	t	Implausibly fast response time (<30s) - possible third-party confirmation
47a8f8c8-e423-47bf-b5b0-78c2c08cbf85	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-11 07:30:00	2026-06-11 08:30:00	f	2026-06-11 08:44:10.148872	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-11	\N	\N	t	f	\N
f6ddb052-eb72-470c-8735-bac1371d0628	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-11 08:00:00	2026-06-11 09:00:00	f	2026-06-11 09:00:02.857711	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-11	\N	\N	t	f	\N
368b2851-3f3a-4be4-a6f8-29574310b56d	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-11 08:00:00	2026-06-11 09:00:00	f	2026-06-11 09:00:02.872796	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-11	\N	\N	t	f	\N
2a846bc9-b37a-4248-8ac4-e9eb06d704dd	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-11 08:00:00	2026-06-11 09:00:00	f	2026-06-11 18:58:17.959341	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-11	\N	\N	t	f	\N
38947f4a-44ad-4b45-8927-827f5a4b066e	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-12 08:00:00	2026-06-12 09:00:00	f	2026-06-12 18:50:59.151991	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-12	\N	\N	t	f	\N
951b503c-54c5-47b2-87f0-25a83da627ea	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-12 08:00:00	2026-06-12 09:00:00	f	2026-06-12 18:51:00.251677	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-12	\N	\N	t	f	\N
b6e71f9f-96d1-4250-97e3-c85f8d80e18d	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-12 07:30:00	2026-06-12 08:30:00	f	2026-06-12 18:51:00.733901	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-12	\N	\N	t	f	\N
f7642f47-dc2b-4de0-bd36-d97ce28a4ef2	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-12 08:00:00	2026-06-12 09:00:00	f	2026-06-12 18:51:01.450658	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-12	\N	\N	t	f	\N
2bf4963e-92c9-4f9b-bd78-122bed07ef87	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-14 08:00:00	2026-06-14 09:00:00	f	2026-06-14 12:20:58.545406	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-14	\N	\N	t	f	\N
2e1cde8e-733f-4879-9628-a64b8adeacff	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-14 08:00:00	2026-06-14 09:00:00	f	2026-06-14 12:21:00.541856	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-14	\N	\N	t	f	\N
66f1f732-f3c8-4ef4-a79f-941be19b9f2a	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-14 07:30:00	2026-06-14 08:30:00	f	2026-06-14 12:21:00.942805	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-14	\N	\N	t	f	\N
211b93cb-eccf-44fe-b6d4-04b000b0f3b0	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-14 08:00:00	2026-06-14 09:00:00	f	2026-06-14 12:21:01.44146	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-14	\N	\N	t	f	\N
5909c0f1-acb4-409b-8f25-e6bc9ba8853c	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-15 07:30:00	2026-06-15 08:30:00	f	2026-06-15 08:35:44.083523	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-15	\N	\N	t	f	\N
c975121c-aa70-47a8-9e6d-357d4f2584d0	8009c775-9249-45bf-8f23-5270418f75d1	APP	\N	2026-06-15 08:00:00	2026-06-15 08:45:00	f	2026-06-15 08:59:44.945247	eab48139-8c15-411c-95ee-0f30d2ddc7a0	a081a016-a773-4a43-9b02-05a758c3f600	2026-06-15	\N	\N	t	f	\N
151f76d2-be33-4bde-8b8a-3d09b900520b	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-15 08:00:00	2026-06-15 09:00:00	f	2026-06-15 09:00:44.936486	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-15	\N	\N	t	f	\N
dfbc41d2-035d-4596-888c-f890fe1a571a	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-15 08:00:00	2026-06-15 09:00:00	f	2026-06-15 09:00:45.048467	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-15	\N	\N	t	f	\N
bc2cc89d-541f-400a-a12d-ece463434621	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-15 08:00:00	2026-06-15 09:00:00	f	2026-06-15 09:00:45.238832	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-15	\N	\N	t	f	\N
bee99d3d-7021-4adc-b024-c692553304db	8009c775-9249-45bf-8f23-5270418f75d1	APP	3842	2026-06-15 08:00:00	2026-06-15 08:45:00	f	2026-06-15 09:04:02.4436	eab48139-8c15-411c-95ee-0f30d2ddc7a0	a081a016-a773-4a43-9b02-05a758c3f600	2026-06-15	2026-06-15 09:04:02.44307	\N	f	f	\N
7b7dc2dd-da4e-4655-b78d-93853c52604f	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-16 08:00:00	2026-06-16 09:00:00	f	2026-06-16 10:01:00.888336	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-16	\N	\N	t	f	\N
af13970c-81cb-4252-b8cc-397742509249	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-16 08:00:00	2026-06-16 09:00:00	f	2026-06-16 10:01:01.788041	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-16	\N	\N	t	f	\N
66ae0fcd-7c82-406c-822e-6f33659be2e8	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-16 07:30:00	2026-06-16 08:30:00	f	2026-06-16 10:01:01.894597	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-16	\N	\N	t	f	\N
0712d5d7-2bcd-4167-9283-d6510f3d580c	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-16 08:00:00	2026-06-16 09:00:00	f	2026-06-16 10:01:02.186519	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-16	\N	\N	t	f	\N
52d22de4-80c1-403a-bf82-74a760510945	8009c775-9249-45bf-8f23-5270418f75d1	APP	\N	2026-06-16 08:00:00	2026-06-16 08:45:00	f	2026-06-16 10:01:02.388504	eab48139-8c15-411c-95ee-0f30d2ddc7a0	a081a016-a773-4a43-9b02-05a758c3f600	2026-06-16	\N	\N	t	f	\N
39268984-381a-41f6-9866-b3263f2166f7	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-18 07:30:00	2026-06-18 08:30:00	f	2026-06-18 08:44:40.523118	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-18	\N	\N	t	f	\N
d747278c-012c-484e-89f5-9aa2dfd5e39f	8009c775-9249-45bf-8f23-5270418f75d1	APP	\N	2026-06-18 08:00:00	2026-06-18 08:45:00	f	2026-06-18 08:45:34.819288	eab48139-8c15-411c-95ee-0f30d2ddc7a0	a081a016-a773-4a43-9b02-05a758c3f600	2026-06-18	\N	\N	t	f	\N
97509d20-1c74-4a23-9bda-91d3d6a3c066	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-18 08:00:00	2026-06-18 09:00:00	f	2026-06-18 09:00:34.434097	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-18	\N	\N	t	f	\N
36f68794-eb71-4b5f-ba06-e1c7614a8b02	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-18 08:00:00	2026-06-18 09:00:00	f	2026-06-18 09:00:34.534954	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-18	\N	\N	t	f	\N
b2cc524a-7c23-408f-9dc9-24ebaca1b8e1	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-18 08:00:00	2026-06-18 09:00:00	f	2026-06-18 09:00:34.728182	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-18	\N	\N	t	f	\N
6e628ab0-ae28-4243-8b3b-56330868ea44	46bb83b4-0890-4763-b54e-1ec649d5062b	APP	\N	2026-06-18 14:00:00	2026-06-18 14:45:00	f	2026-06-18 14:53:18.097141	d0566c6a-1595-4222-8bc1-f4f037627c0f	47001056-bccb-44d8-83fe-f72f1cacdc75	2026-06-18	\N	\N	t	f	\N
890f137c-0ed4-4362-8e5b-68cdcc01cce1	bfb73641-679c-4161-8587-b48754d8ea9b	APP	\N	2026-06-18 08:00:00	2026-06-18 08:45:00	f	2026-06-18 23:04:25.734865	61d23afc-09dc-49f2-9524-114207471d12	25adf19c-6678-4e01-a082-c2d0e03a7868	2026-06-18	\N	\N	t	f	\N
fd4b2121-10b5-4919-a94c-6d78a4363150	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	-8044	2026-06-19 08:00:00	2026-06-19 09:00:00	f	2026-06-19 05:45:56.252164	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-19	2026-06-19 05:45:56.165085	\N	f	f	\N
a5dd9547-5496-4cc1-a362-c26c2bacb2df	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-19 07:30:00	2026-06-19 08:30:00	f	2026-06-19 08:30:01.675032	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-19	\N	\N	t	f	\N
023ceb4f-e6aa-4acd-8091-4491e44da141	8009c775-9249-45bf-8f23-5270418f75d1	APP	\N	2026-06-19 08:00:00	2026-06-19 08:45:00	f	2026-06-19 08:45:01.674318	eab48139-8c15-411c-95ee-0f30d2ddc7a0	a081a016-a773-4a43-9b02-05a758c3f600	2026-06-19	\N	\N	t	f	\N
6e207292-4e4e-420b-9d6a-014797c36a31	bfb73641-679c-4161-8587-b48754d8ea9b	APP	\N	2026-06-19 08:00:00	2026-06-19 08:45:00	f	2026-06-19 08:45:01.869433	61d23afc-09dc-49f2-9524-114207471d12	25adf19c-6678-4e01-a082-c2d0e03a7868	2026-06-19	\N	\N	t	f	\N
b44bfa94-01bf-4915-81a7-74e75eeddd3e	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-19 08:00:00	2026-06-19 09:00:00	f	2026-06-19 09:00:01.669577	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-19	\N	\N	t	f	\N
57b9afa2-b873-4988-9e10-73ae559931e5	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-19 08:00:00	2026-06-19 09:00:00	f	2026-06-19 09:00:01.969705	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-19	\N	\N	t	f	\N
01afdd77-b3f6-44fb-9d54-fe3ca1fc1044	46bb83b4-0890-4763-b54e-1ec649d5062b	APP	\N	2026-06-19 14:00:00	2026-06-19 14:45:00	f	2026-06-19 23:50:15.926427	d0566c6a-1595-4222-8bc1-f4f037627c0f	47001056-bccb-44d8-83fe-f72f1cacdc75	2026-06-19	\N	\N	t	f	\N
c5a7c7dc-4b02-4d64-b866-c047a482b7d5	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-20 08:00:00	2026-06-20 09:00:00	f	2026-06-20 16:16:50.065026	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-20	\N	\N	t	f	\N
8c3402ad-342c-4bc7-a247-eb41f67b33a1	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-20 08:00:00	2026-06-20 09:00:00	f	2026-06-20 16:16:55.170861	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-20	\N	\N	t	f	\N
53b6c001-f150-44bf-a8ca-ef74f4432fc7	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-20 07:30:00	2026-06-20 08:30:00	f	2026-06-20 16:16:55.861822	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-20	\N	\N	t	f	\N
9d7269a5-6162-4bee-8c54-7c1c39cceb4a	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-20 08:00:00	2026-06-20 09:00:00	f	2026-06-20 16:16:56.55967	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-20	\N	\N	t	f	\N
a884470a-6321-4439-86b1-c0d919933bcd	8009c775-9249-45bf-8f23-5270418f75d1	APP	\N	2026-06-20 08:00:00	2026-06-20 08:45:00	f	2026-06-20 16:16:57.072027	eab48139-8c15-411c-95ee-0f30d2ddc7a0	a081a016-a773-4a43-9b02-05a758c3f600	2026-06-20	\N	\N	t	f	\N
8cf6d1a0-6ced-4da5-a39d-29d50fad98be	46bb83b4-0890-4763-b54e-1ec649d5062b	APP	\N	2026-06-20 14:00:00	2026-06-20 14:45:00	f	2026-06-20 16:16:57.56469	d0566c6a-1595-4222-8bc1-f4f037627c0f	47001056-bccb-44d8-83fe-f72f1cacdc75	2026-06-20	\N	\N	t	f	\N
1288509e-1ae7-46c3-bfd2-7b6debb96f47	bfb73641-679c-4161-8587-b48754d8ea9b	APP	\N	2026-06-20 08:00:00	2026-06-20 08:45:00	f	2026-06-20 16:16:58.068496	61d23afc-09dc-49f2-9524-114207471d12	25adf19c-6678-4e01-a082-c2d0e03a7868	2026-06-20	\N	\N	t	f	\N
308edded-2551-4da5-9862-3ae9fb4bff03	9034f0b1-8d54-4d8a-83c3-f723077062b2	APP	\N	2026-06-21 08:00:00	2026-06-21 09:00:00	f	2026-06-21 19:47:49.938126	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	e5b65fc3-f221-4d13-be59-d7d53f7204b7	2026-06-21	\N	\N	t	f	\N
3c8ce822-a4a2-4a70-bbd1-858f09846ef1	49ca4358-c32e-44ba-bc52-92d731ea8f5b	APP	\N	2026-06-21 08:00:00	2026-06-21 09:00:00	f	2026-06-21 19:47:52.535041	736698df-631a-4c49-84d9-1483d1773751	31d48aa2-c914-493c-8ce3-d23029abe814	2026-06-21	\N	\N	t	f	\N
c4181f72-37ab-40e6-b360-549fba5c7a1c	6b2a9c4a-9845-406f-bf96-5e92606e3309	APP	\N	2026-06-21 07:30:00	2026-06-21 08:30:00	f	2026-06-21 19:47:52.933968	7381bc34-97bb-4390-8e1d-131bd6f201d7	16def4e8-12c9-4912-832c-514863156657	2026-06-21	\N	\N	t	f	\N
64272423-926d-4f1b-9bc6-0924a7e428cc	dce2bd61-cee2-4aac-a559-d537bbd6d175	APP	\N	2026-06-21 08:00:00	2026-06-21 09:00:00	f	2026-06-21 19:47:53.336616	0df3febf-4104-4319-ab8d-cdb29f9cb384	782149dd-7835-406c-a0df-48cd58f87102	2026-06-21	\N	\N	t	f	\N
be03a921-d46c-4774-8d6b-b7478c9a8202	8009c775-9249-45bf-8f23-5270418f75d1	APP	\N	2026-06-21 08:00:00	2026-06-21 08:45:00	f	2026-06-21 19:47:53.938649	eab48139-8c15-411c-95ee-0f30d2ddc7a0	a081a016-a773-4a43-9b02-05a758c3f600	2026-06-21	\N	\N	t	f	\N
e8844071-e042-42e5-8bfe-9d75be4607dc	46bb83b4-0890-4763-b54e-1ec649d5062b	APP	\N	2026-06-21 14:00:00	2026-06-21 14:45:00	f	2026-06-21 19:47:54.833014	d0566c6a-1595-4222-8bc1-f4f037627c0f	47001056-bccb-44d8-83fe-f72f1cacdc75	2026-06-21	\N	\N	t	f	\N
eccdbde5-985b-4b25-bf58-7a19858b1025	bfb73641-679c-4161-8587-b48754d8ea9b	APP	\N	2026-06-21 08:00:00	2026-06-21 08:45:00	f	2026-06-21 19:47:55.83919	61d23afc-09dc-49f2-9524-114207471d12	25adf19c-6678-4e01-a082-c2d0e03a7868	2026-06-21	\N	\N	t	f	\N
\.


--
-- Data for Name: dose_schedules; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.dose_schedules (id, plan_id, patient_id, dose_time, dose_label, notification_method, window_duration_minutes, is_active, created_by, created_at, prescription_source) FROM stdin;
e5b65fc3-f221-4d13-be59-d7d53f7204b7	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	9034f0b1-8d54-4d8a-83c3-f723077062b2	08:00:00	Morning dose	APP	60	t	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-10 10:55:13.719812	Dream Medical Center pharmacy
31d48aa2-c914-493c-8ce3-d23029abe814	736698df-631a-4c49-84d9-1483d1773751	49ca4358-c32e-44ba-bc52-92d731ea8f5b	08:00:00	Morning dose	APP	60	t	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-10 17:16:22.221024	Dream Medical Center pharmacy
16def4e8-12c9-4912-832c-514863156657	7381bc34-97bb-4390-8e1d-131bd6f201d7	6b2a9c4a-9845-406f-bf96-5e92606e3309	07:30:00	Morning dose	APP	60	t	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-10 17:16:31.022742	Dream Medical Center pharmacy
782149dd-7835-406c-a0df-48cd58f87102	0df3febf-4104-4319-ab8d-cdb29f9cb384	dce2bd61-cee2-4aac-a559-d537bbd6d175	08:00:00	Morning dose	APP	60	t	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-11 18:57:49.958889	Dream Medical Center pharmacy
a081a016-a773-4a43-9b02-05a758c3f600	eab48139-8c15-411c-95ee-0f30d2ddc7a0	8009c775-9249-45bf-8f23-5270418f75d1	08:00:00	Morning dose	APP	45	t	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-15 08:58:45.535896	\N
47001056-bccb-44d8-83fe-f72f1cacdc75	d0566c6a-1595-4222-8bc1-f4f037627c0f	46bb83b4-0890-4763-b54e-1ec649d5062b	14:00:00	\N	APP	45	t	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-18 11:10:11.242285	\N
25adf19c-6678-4e01-a082-c2d0e03a7868	61d23afc-09dc-49f2-9524-114207471d12	bfb73641-679c-4161-8587-b48754d8ea9b	08:00:00	\N	APP	45	t	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	2026-06-18 23:04:10.210504	\N
\.


--
-- Data for Name: facilities; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.facilities (id, name, location, district, fhir_endpoint_url, is_active, created_at) FROM stdin;
c40666ca-46c3-45d7-a3b1-e25faa8f0126	Dream Medical Center	KG 123 St, Kigali	Gasabo	\N	t	2026-05-30 12:10:42.465363
\.


--
-- Data for Name: facility_providers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.facility_providers (id, user_id, facility_id, specialization, license_number, created_at) FROM stdin;
56f20695-337b-42d9-becf-f7c675724aa6	d40c82fe-c3f5-4105-be8a-b95b7308077f	c40666ca-46c3-45d7-a3b1-e25faa8f0126	Infectious Disease Specialist	\N	2026-05-30 17:41:55.970945
1b8b109f-ef8f-44bb-b7a5-484a09d39769	786e213d-d2bd-4541-883f-73e3b51e7da0	c40666ca-46c3-45d7-a3b1-e25faa8f0126	Infectious diseases	string	2026-06-05 11:36:52.685192
739f4912-a76b-4515-9f77-8195d0f0393e	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	c40666ca-46c3-45d7-a3b1-e25faa8f0126	\N	\N	2026-06-18 20:15:20.114905
\.


--
-- Data for Name: fhir_sync_logs; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.fhir_sync_logs (id, chw_id, sync_started_at, sync_completed_at, records_synced, records_failed, sync_status, error_log, created_at) FROM stdin;
8291ba01-cbcf-437e-b94f-b56f34f62084	\N	2026-06-10 19:18:13.573944	2026-06-10 19:18:39.469525	21	0	COMPLETED	\n[notify_error] The read operation timed out	2026-06-10 19:18:13.573944
6b548a06-4068-4efa-a1d6-6115cb0169b4	\N	2026-06-10 19:24:51.446989	2026-06-10 19:25:06.244548	21	0	COMPLETED	\N	2026-06-10 19:24:51.446989
acaee584-a4ac-4eb8-9050-a9c49685666a	\N	2026-06-11 19:07:58.659477	2026-06-11 19:08:17.498429	0	2	FAILED	Patient PT-F952B170: [WinError 10061] No connection could be made because the target machine actively refused it\nTreatmentPlan 0df3febf-4104-4319-ab8d-cdb29f9cb384: patient not yet synced, skipping\n[notify_error] [Errno 11001] getaddrinfo failed	2026-06-11 19:07:58.659477
ba8ac6b9-02b3-4bb9-b967-4796ed9a7d0c	\N	2026-06-11 19:21:22.650733	2026-06-11 19:22:55.621149	0	2	FAILED	Patient PT-F952B170: Server disconnected without sending a response.\nTreatmentPlan 0df3febf-4104-4319-ab8d-cdb29f9cb384: patient not yet synced, skipping\n[notify_error] The read operation timed out	2026-06-11 19:21:22.650733
972477aa-a8b4-48a0-92e7-c2ed550a9954	\N	2026-06-11 19:58:18.238931	\N	0	0	IN_PROGRESS	\N	2026-06-11 19:58:18.238931
2f93148f-6565-4901-a81a-69fc1f6dd873	\N	2026-06-11 20:05:21.586093	2026-06-11 20:05:37.088191	2	0	COMPLETED	\N	2026-06-11 20:05:21.586093
264e827e-fe7b-4d8a-b37f-91be8f34b46c	\N	2026-06-13 07:28:46.895257	2026-06-13 07:30:35.882715	0	12	FAILED	Patient PT-EEF83CCE: [WinError 10061] No connection could be made because the target machine actively refused it\nPatient PT-BD5C2489: [WinError 10061] No connection could be made because the target machine actively refused it\nPatient PT-F952B170: [WinError 10061] No connection could be made because the target machine actively refused it\nHomeVisit 5197d6b8-899a-4b4b-88ff-bd78436c2c9b: patient not yet synced to FHIR, skipping\nHomeVisit 4ff33593-7368-4c6d-ba6d-322b16c80569: patient not yet synced to FHIR, skipping\nHomeVisit 78459b00-6323-4ace-9263-dec75310e2f5: patient not yet synced to FHIR, skipping\nHomeVisit 36bd52c6-84eb-48b3-b65c-c5988f57e37e: patient not yet synced to FHIR, skipping\nHomeVisit dd57587e-1441-4757-8189-05fae7c0b914: patient not yet synced to FHIR, skipping\nHomeVisit 5f6cf3aa-c795-4f36-8aed-b44730640617: patient not yet synced to FHIR, skipping\nTreatmentPlan a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e: patient not yet synced, skipping\nTreatmentPlan 736698df-631a-4c49-84d9-1483d1773751: patient not yet synced, skipping\nTreatmentPlan 0df3febf-4104-4319-ab8d-cdb29f9cb384: patient not yet synced, skipping\n[notify_error] The read operation timed out	2026-06-13 07:28:46.895257
18f70c62-3156-442d-9ce4-8547043cc6d3	\N	2026-06-13 07:47:56.765187	2026-06-13 07:49:37.931724	12	0	COMPLETED	\n[notify_error] The read operation timed out	2026-06-13 07:47:56.765187
\.


--
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	create core tables	SQL	V1__create_core_tables.sql	228349742	hivtb_db_user	2026-05-30 12:08:53.259785	640	t
2	2	add dose schedules update schema	SQL	V2__add_dose_schedules_update_schema.sql	1200176529	hivtb_db_user	2026-05-30 12:08:55.459445	404	t
3	3	add patient user link	SQL	V3__add_patient_user_link.sql	-1663092480	hivtb_db_user	2026-05-30 12:08:56.260844	106	t
4	4	add must change password	SQL	V4__add_must_change_password.sql	-125150884	hivtb_db_user	2026-05-30 12:08:56.568364	95	t
5	5	create referral table	SQL	V5__create_referral_table.sql	1507490216	hivtb_db_user	2026-05-30 12:08:56.860732	113	t
6	6	ltfu tracing and enum updates	SQL	V6__ltfu_tracing_and_enum_updates.sql	1910922858	hivtb_db_user	2026-06-02 11:28:33.643429	712	t
7	7	add fcm token	SQL	V7__add_fcm_token.sql	-792857418	hivtb_db_user	2026-06-03 10:22:54.580547	397	t
8	8	dose schedule prescription source	SQL	V8__dose_schedule_prescription_source.sql	-2064363143	hivtb_db_user	2026-06-03 10:22:55.884924	203	t
9	9	patient registration routes	SQL	V9__patient_registration_routes.sql	2095126314	hivtb_db_user	2026-06-03 10:22:56.379657	118	t
10	10	add alert resolved by	SQL	V10__add_alert_resolved_by.sql	-104566037	hivtb_db_user	2026-06-11 19:11:40.428887	301	t
11	11	add ltfu tracing resolved alert type	SQL	V11__add_ltfu_tracing_resolved_alert_type.sql	2087739633	hivtb_db_user	2026-06-11 19:11:42.887652	101	t
12	12	add account lockout fields	SQL	V12__add_account_lockout_fields.sql	81803352	hivtb_db_user	2026-06-11 19:11:43.287806	102	t
13	13	add home visit status	SQL	V13__add_home_visit_status.sql	-1382918900	hivtb_db_user	2026-06-18 18:46:25.590696	203	t
14	14	widen suspicion reason	SQL	V14__widen_suspicion_reason.sql	-1106417822	hivtb_db_user	2026-06-18 18:46:26.798638	109	t
15	15	create system settings	SQL	V15__create_system_settings.sql	-1869062043	hivtb_db_user	2026-06-18 18:46:27.202819	120	t
16	16	add home visit client request id	SQL	V16__add_home_visit_client_request_id.sql	411556681	hivtb_db_user	2026-06-18 18:46:27.596503	106	t
17	17	add sync failure alert type	SQL	V17__add_sync_failure_alert_type.sql	16293079	hivtb_db_user	2026-06-20 18:53:24.21731	403	t
18	18	create lab results	SQL	V18__create_lab_results.sql	390587515	hivtb_db_user	2026-06-20 18:53:25.615081	207	t
19	19	create locations	SQL	V19__create_locations.sql	270058948	hivtb_db_user	2026-06-20 18:53:26.227296	207	t
20	20	add chw assignment acceptance	SQL	V20__add_chw_assignment_acceptance.sql	-1100026519	hivtb_db_user	2026-06-20 18:53:26.725658	14	t
21	21	add new patient assignment alert type	SQL	V21__add_new_patient_assignment_alert_type.sql	441300911	hivtb_db_user	2026-06-20 18:53:27.216532	107	t
\.


--
-- Data for Name: home_visits; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.home_visits (id, patient_id, chw_id, visit_date, adherence_status, pill_count_recorded, pill_count_expected, pill_count_discrepancy, symptoms_reported, side_effects_reported, psychosocial_notes, next_visit_date, fhir_observation_id, sync_status, created_at, visit_status, client_request_id) FROM stdin;
83287823-040f-456c-8fcd-edd7a32bb755	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-05-30 23:46:25.120155	GOOD	\N	\N	f	Fever	rash	Patient seems anxious	2026-06-27 00:00:00	1032	SYNCED	2026-05-30 21:46:26.615249	ATTENDED_TO	\N
bf60c432-4d44-4f51-88a7-a2085962cc90	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-02 19:17:05.293505	FAIR	20	24	t	\N	Mild nausea reported, advised to take with food.	\N	\N	1035	SYNCED	2026-06-02 19:17:05.293505	ATTENDED_TO	\N
fffe9389-480f-423a-852d-e117bf836b79	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	2026-05-19 19:17:05.615513	FAIR	14	14	f	\N	\N	\N	\N	1036	SYNCED	2026-05-19 19:17:05.615513	ATTENDED_TO	\N
5197d6b8-899a-4b4b-88ff-bd78436c2c9b	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	2026-05-31 19:16:52.338982	GOOD	28	28	f	\N	\N	\N	\N	\N	PENDING	2026-05-31 19:16:52.338982	ATTENDED_TO	\N
4ff33593-7368-4c6d-ba6d-322b16c80569	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	2026-05-17 19:16:52.684107	GOOD	14	14	f	\N	\N	\N	\N	\N	PENDING	2026-05-17 19:16:52.684107	ATTENDED_TO	\N
78459b00-6323-4ace-9263-dec75310e2f5	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-06 19:17:08.448137	POOR	18	30	t	Cough still present, appears fatigued.	Patient reports persistent nausea and dizziness.	\N	\N	\N	PENDING	2026-06-06 19:17:08.448137	ATTENDED_TO	\N
36bd52c6-84eb-48b3-b65c-c5988f57e37e	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	2026-05-30 19:17:08.776384	POOR	22	30	t	\N	Patient reports loss of appetite.	\N	\N	\N	PENDING	2026-05-30 19:17:08.776384	ATTENDED_TO	\N
dd57587e-1441-4757-8189-05fae7c0b914	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-11 23:09:58.979057	GOOD	20	10	t	Fever,cough, fatigue	Nausea, dizziness	\N	2026-06-18 00:00:00	\N	PENDING	2026-06-11 21:09:59.261198	ATTENDED_TO	\N
5f6cf3aa-c795-4f36-8aed-b44730640617	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-11 23:42:29.281613	PARTIAL	20	30	t	Fever	Dizziness and rash	Family support observed	2026-06-18 00:00:00	\N	PENDING	2026-06-11 21:42:30.060865	ATTENDED_TO	\N
aedd50e5-2a44-4a7a-abd4-534e8b7d93ff	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-18 12:42:21.77356	PARTIAL	\N	\N	f	Fever	\N	Family support observed	2026-06-25 00:00:00	\N	PENDING	2026-06-18 10:42:25.442311	ATTENDED_TO	\N
9b4b2b93-dabc-47ed-a31f-8389afc1d42c	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-18 12:44:28.524027	PARTIAL	25	20	t	Fatigue	Rash	Patient seems anxious	2026-06-26 00:00:00	\N	PENDING	2026-06-18 10:44:30.343981	ATTENDED_TO	\N
8debce31-4d28-4f70-afec-0d4896e96511	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-18 12:46:03.695763	GOOD	\N	\N	f	\N	\N	\N	\N	\N	PENDING	2026-06-18 10:46:05.946591	ATTENDED_TO	\N
68e6d8b0-ba2a-4b19-93e0-30bd7bad60b6	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-18 12:46:13.509606	GOOD	\N	\N	f	\N	\N	\N	\N	\N	PENDING	2026-06-18 10:46:15.053931	ATTENDED_TO	\N
ab996a7f-f8b0-4925-825b-78f3353eed2e	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-19 05:00:18.371	GOOD	20	10	t	Fever	Dizzy	Family support observed	2026-06-26 00:00:00	\N	PENDING	2026-06-19 05:00:19.824163	ATTENDED_TO	dda857cf-e5d1-4223-a964-90fec21181e7
\.


--
-- Data for Name: lab_results; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.lab_results (id, patient_id, loinc_code, value, unit, observed_at, fhir_observation_id, created_at) FROM stdin;
\.


--
-- Data for Name: locations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.locations (id, name, code, description, location_type, parent_id, population, area_km2, village_chief) FROM stdin;
\.


--
-- Data for Name: medication_records; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.medication_records (id, patient_id, period_start, period_end, doses_scheduled, adherence_pct, below_threshold, sync_status, plan_id, doses_confirmed, doses_verified, false_confirmation_flag, fhir_statement_id, updated_at) FROM stdin;
1	bfb73641-679c-4161-8587-b48754d8ea9b	2026-06-18	2026-06-18	1	0.00	t	PENDING	61d23afc-09dc-49f2-9524-114207471d12	0	0	f	\N	2026-06-18 23:04:26.421834
2	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-16	2026-05-16	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:49:57.121566
3	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-14	2026-05-14	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:49:57.626159
4	46bb83b4-0890-4763-b54e-1ec649d5062b	2026-06-18	2026-06-18	1	0.00	t	PENDING	d0566c6a-1595-4222-8bc1-f4f037627c0f	0	0	f	\N	2026-06-19 04:49:58.120773
5	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-15	2026-05-15	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:49:58.327231
6	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-31	2026-05-31	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:49:58.524594
7	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-28	2026-05-28	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:49:58.822935
8	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-23	2026-05-23	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:49:59.02806
9	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-13	2026-05-13	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:49:59.224175
10	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-18	2026-06-18	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:49:59.422445
11	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-22	2026-05-22	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:49:59.72366
12	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-11	2026-06-11	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:00.027924
13	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-02	2026-06-02	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:00.23058
14	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-19	2026-05-19	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:00.629639
15	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-20	2026-05-20	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:01.029493
16	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-14	2026-06-14	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:01.329266
17	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-04	2026-06-04	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:01.626228
18	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-25	2026-05-25	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:01.922276
19	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-03	2026-06-03	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:50:02.427691
20	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-16	2026-06-16	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:02.62699
21	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-14	2026-05-14	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:02.924179
22	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-21	2026-05-21	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:03.224599
23	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-16	2026-05-16	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:03.435001
24	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-18	2026-06-18	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-19 04:50:03.824514
25	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-12	2026-06-12	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-19 04:50:04.122645
26	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-07	2026-06-07	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:04.323247
27	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-05	2026-06-05	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:04.522182
28	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-15	2026-05-15	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:04.722453
29	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-09	2026-06-09	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:05.024553
30	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-27	2026-05-27	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:05.224872
31	8009c775-9249-45bf-8f23-5270418f75d1	2026-06-15	2026-06-15	1	100.00	f	PENDING	eab48139-8c15-411c-95ee-0f30d2ddc7a0	1	1	f	\N	2026-06-19 04:50:05.425471
32	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-20	2026-05-20	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:05.62655
33	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-29	2026-05-29	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:05.924701
34	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-15	2026-06-15	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-19 04:50:06.125507
35	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-05	2026-06-05	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:06.322195
36	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-07	2026-06-07	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:06.521475
37	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-10	2026-06-10	1	100.00	f	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	1	1	t	\N	2026-06-19 04:50:06.632682
38	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-24	2026-05-24	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:06.826076
39	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-31	2026-05-31	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:07.127413
40	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-11	2026-06-11	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-19 04:50:07.424812
41	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-09	2026-06-09	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:07.62604
42	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-02	2026-06-02	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	0	f	\N	2026-06-19 04:50:07.934994
43	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-18	2026-06-18	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:50:08.430612
44	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-18	2026-05-18	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:08.723679
45	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-12	2026-06-12	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:08.928862
46	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-30	2026-05-30	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:09.222473
47	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-28	2026-05-28	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:09.23233
48	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-11	2026-06-11	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:50:09.424764
49	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-22	2026-05-22	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:09.526425
50	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-09	2026-06-09	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:09.823189
51	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-17	2026-05-17	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:09.929154
52	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-06	2026-06-06	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:10.122127
53	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-30	2026-05-30	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:10.22557
54	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-01	2026-06-01	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:10.524827
55	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-08	2026-06-08	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:10.729464
56	8009c775-9249-45bf-8f23-5270418f75d1	2026-06-18	2026-06-18	1	0.00	t	PENDING	eab48139-8c15-411c-95ee-0f30d2ddc7a0	0	0	f	\N	2026-06-19 04:50:10.92461
57	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-06	2026-06-06	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:11.322593
58	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-23	2026-05-23	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:11.525039
59	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-04	2026-06-04	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:11.725803
60	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-11	2026-05-11	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:11.82903
61	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-17	2026-05-17	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:12.027094
62	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-25	2026-05-25	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:12.32406
63	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-04	2026-06-04	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:12.525671
64	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-15	2026-06-15	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:12.72682
65	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-26	2026-05-26	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:12.836188
66	8009c775-9249-45bf-8f23-5270418f75d1	2026-06-16	2026-06-16	1	0.00	t	PENDING	eab48139-8c15-411c-95ee-0f30d2ddc7a0	0	0	f	\N	2026-06-19 04:50:13.022494
67	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-11	2026-06-11	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:13.03561
68	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-16	2026-06-16	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:50:13.225807
69	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-14	2026-06-14	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:13.423895
70	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-13	2026-05-13	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:13.622435
71	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-27	2026-05-27	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:13.825276
72	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-19	2026-05-19	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:14.123659
73	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-15	2026-06-15	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:14.231185
74	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-21	2026-05-21	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:14.524006
75	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-12	2026-06-12	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:14.725892
76	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-11	2026-05-11	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:14.932378
77	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-16	2026-06-16	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-19 04:50:15.1286
78	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-01	2026-06-01	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:15.325103
79	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-05	2026-06-05	1	100.00	f	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	1	1	f	\N	2026-06-19 04:50:15.524419
80	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-07	2026-06-07	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:15.733395
81	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-12	2026-05-12	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:16.027163
82	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-15	2026-06-15	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:50:16.224346
83	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-08	2026-06-08	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:16.330732
84	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-18	2026-05-18	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:16.523339
85	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-24	2026-05-24	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:16.727764
86	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-14	2026-06-14	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-19 04:50:16.831442
87	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-08	2026-06-08	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:17.124801
88	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-18	2026-06-18	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 04:50:17.325786
89	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-16	2026-06-16	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 04:50:17.625154
90	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-12	2026-06-12	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:50:17.825295
91	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-05-29	2026-05-29	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:18.125206
92	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-12	2026-05-12	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:18.326299
93	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-06	2026-06-06	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:18.529527
94	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-14	2026-06-14	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-19 04:50:18.727295
95	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-03	2026-06-03	1	100.00	f	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	1	1	f	\N	2026-06-19 04:50:18.924443
96	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-05-26	2026-05-26	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 04:50:18.938148
97	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-19	2026-06-19	1	100.00	f	PENDING	736698df-631a-4c49-84d9-1483d1773751	1	1	f	\N	2026-06-19 05:45:56.754668
98	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-19	2026-06-19	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-19 08:30:02.463576
99	8009c775-9249-45bf-8f23-5270418f75d1	2026-06-19	2026-06-19	1	0.00	t	PENDING	eab48139-8c15-411c-95ee-0f30d2ddc7a0	0	0	f	\N	2026-06-19 08:45:01.863041
100	bfb73641-679c-4161-8587-b48754d8ea9b	2026-06-19	2026-06-19	1	0.00	t	PENDING	61d23afc-09dc-49f2-9524-114207471d12	0	0	f	\N	2026-06-19 08:45:02.065627
101	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-19	2026-06-19	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-19 09:00:01.872566
102	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-19	2026-06-19	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-19 09:00:02.072005
103	46bb83b4-0890-4763-b54e-1ec649d5062b	2026-06-19	2026-06-19	1	0.00	t	PENDING	d0566c6a-1595-4222-8bc1-f4f037627c0f	0	0	f	\N	2026-06-19 23:50:24.734933
104	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-20	2026-06-20	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-20 16:16:54.997907
105	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-20	2026-06-20	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-20 16:16:55.666851
106	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-20	2026-06-20	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-20 16:16:56.265938
107	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-20	2026-06-20	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-20 16:16:56.964551
108	8009c775-9249-45bf-8f23-5270418f75d1	2026-06-20	2026-06-20	1	0.00	t	PENDING	eab48139-8c15-411c-95ee-0f30d2ddc7a0	0	0	f	\N	2026-06-20 16:16:57.460501
109	46bb83b4-0890-4763-b54e-1ec649d5062b	2026-06-20	2026-06-20	1	0.00	t	PENDING	d0566c6a-1595-4222-8bc1-f4f037627c0f	0	0	f	\N	2026-06-20 16:16:57.965261
110	bfb73641-679c-4161-8587-b48754d8ea9b	2026-06-20	2026-06-20	1	0.00	t	PENDING	61d23afc-09dc-49f2-9524-114207471d12	0	0	f	\N	2026-06-20 16:16:58.364516
111	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-21	2026-06-21	1	0.00	t	PENDING	a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	0	0	f	\N	2026-06-21 19:47:52.238107
112	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2026-06-21	2026-06-21	1	0.00	t	PENDING	736698df-631a-4c49-84d9-1483d1773751	0	0	f	\N	2026-06-21 19:47:52.836178
113	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-06-21	2026-06-21	1	0.00	t	PENDING	7381bc34-97bb-4390-8e1d-131bd6f201d7	0	0	f	\N	2026-06-21 19:47:53.236104
114	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-21	2026-06-21	1	0.00	t	PENDING	0df3febf-4104-4319-ab8d-cdb29f9cb384	0	0	f	\N	2026-06-21 19:47:53.73715
115	8009c775-9249-45bf-8f23-5270418f75d1	2026-06-21	2026-06-21	1	0.00	t	PENDING	eab48139-8c15-411c-95ee-0f30d2ddc7a0	0	0	f	\N	2026-06-21 19:47:54.438559
116	46bb83b4-0890-4763-b54e-1ec649d5062b	2026-06-21	2026-06-21	1	0.00	t	PENDING	d0566c6a-1595-4222-8bc1-f4f037627c0f	0	0	f	\N	2026-06-21 19:47:55.735639
117	bfb73641-679c-4161-8587-b48754d8ea9b	2026-06-21	2026-06-21	1	0.00	t	PENDING	61d23afc-09dc-49f2-9524-114207471d12	0	0	f	\N	2026-06-21 19:47:57.134629
\.


--
-- Data for Name: patients; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.patients (id, patient_code, full_name, date_of_birth, sex, national_id, phone_number, has_smartphone, diagnosis_type, art_start_date, tb_treatment_start_date, household_location, village, sector, district, chw_id, facility_id, fhir_patient_id, sync_status, is_active, created_at, updated_at, user_id, registration_route, registration_status, referral_id, screened_by_chw_id, screened_at, confirmed_by, confirmed_at, suspected_condition, screening_symptoms, screening_notes, lab_result_notes, province, cell, chw_assignment_status, chw_assigned_at, chw_accepted_at, chw_assignment_reminder_sent_at, chw_assignment_escalated_at) FROM stdin;
9034f0b1-8d54-4d8a-83c3-f723077062b2	PT-EEF83CCE	Jean Damascene	1989-03-15	MALE	1198920067788321	+250798422578	t	HIV_TB_COINFECTION	2026-06-10	2026-06-10	Near Amahoro Stadium, Plot 245	Amahoro	Remera	Gasabo	1dd0bb68-cd37-4945-846c-8309613c438a	c40666ca-46c3-45d7-a3b1-e25faa8f0126	\N	PENDING	t	2026-06-10 09:05:08.608755	2026-06-10 19:25:06.342152	b23bc14f-1785-4679-bdbe-81f0c1b779f2	CHW_SCREENING	ACTIVE	REF-2026-REM-9733	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-10 09:05:08.607934	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-10 10:24:19.137916	HIV_TB_COINFECTION	persistent cough,weight loss,night sweats	Patient referred by community health volunteer after household contact tracing. Reports cough lasting 3 weeks.	CD4 count: 180 cells/mm3. GeneXpert: MTB detected, rifampicin sensitive. HIV rapid test: reactive, confirmed by ELISA.	Kigali	Nyabisindu	ACCEPTED	\N	\N	\N	\N
96fbf0ac-9af5-439b-9730-1d8198fcddea	PT-F56814DE	Innocent Nishimwe	2001-06-05	MALE	\N	+250788312512	t	HIV	\N	\N	\N	Gatsibo	\N	Kicukiro	02a4d824-e859-45e5-9092-40dc69fa4c89	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1021	SYNCED	t	2026-05-30 16:49:11.606762	2026-06-10 19:25:06.247935	3414701f-3738-4f15-8e0e-e84cd8692388	FACILITY	ACTIVE	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	\N	ACCEPTED	\N	\N	\N	\N
b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	PT-42B6C368	Valentine Niyonsenga	1996-06-06	female	1199620012001200	+250783850296	t	HIV	2026-06-06	2026-06-06	string	Rugwiro	Kimironko	Gasabo	02a4d824-e859-45e5-9092-40dc69fa4c89	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1023	SYNCED	t	2026-06-06 17:09:37.496483	2026-06-10 19:25:06.248231	74e6bcd2-d381-4bc4-988f-99e7948929e1	CHW_SCREENING	ACTIVE	REF-2026-KIM-7064	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-06 17:09:37.495435	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-06 17:22:18.99406	HIV	string	string	CD4 count 350, VL 45000	Kigali	Kanzenze	ACCEPTED	\N	\N	\N	\N
0ffb9432-cc2a-4814-aa7a-488551497730	PT-1A32F7C1	Test Patient SMS	2000-06-09	Female	\N	+250783850296	t	TB	\N	\N	string	Kabaha	Nyamata	Bugesera	02a4d824-e859-45e5-9092-40dc69fa4c89	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1024	SYNCED	t	2026-06-09 07:56:12.155681	2026-06-10 19:25:06.248353	\N	CHW_SCREENING	PROVISIONAL	REF-2026-NYA-2345	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-09 07:56:12.062233	\N	\N	TB	several coughing	string	\N	East	Kayumba	ACCEPTED	\N	\N	\N	\N
e0f9a10b-4b32-49c7-bb9d-fb8df101610f	PT-ED686138	Test SMS	2001-06-09	Female	1200179982679980	+25079129125	t	TB	2026-06-09	2026-06-09	string	Kabaha	Nyamata	Bugesera	02a4d824-e859-45e5-9092-40dc69fa4c89	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1025	SYNCED	t	2026-06-09 08:12:14.170686	2026-06-10 19:25:06.248473	bfc6d975-2caf-4940-abb7-fb0598fb9d17	CHW_SCREENING	ACTIVE	REF-2026-NYA-3750	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-09 08:12:14.169925	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-09 08:14:10.761936	TB	several coughing	string	string	East	Kayumba	ACCEPTED	\N	\N	\N	\N
46bb83b4-0890-4763-b54e-1ec649d5062b	PT-16836520	SMS	2002-06-09	Female	\N	+250791291251	t	HIV	\N	\N	string	string	Gasabo	Kigali	02a4d824-e859-45e5-9092-40dc69fa4c89	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1026	SYNCED	t	2026-06-09 08:52:03.308836	2026-06-10 19:25:06.341893	\N	CHW_SCREENING	PROVISIONAL	REF-2026-GAS-9133	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-09 08:52:03.308035	\N	\N	HIV	Sore throat and painful mouth ulcers	string	\N	Kigali	string	ACCEPTED	\N	\N	\N	\N
6b86e729-1dd2-4de7-8ec6-4382838cf376	PT-48DE087F	Niyonzima Paul	1993-04-22	Male	\N	+250789567890	t	HIV	\N	\N	Rural hillside community	Rugarama	Niboye	Kicukiro	02a4d824-e859-45e5-9092-40dc69fa4c89	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1027	SYNCED	t	2026-06-09 08:59:48.503238	2026-06-10 19:25:06.342054	\N	CHW_SCREENING	PROVISIONAL	REF-2026-NIB-8400	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-09 08:59:48.409255	\N	\N	HIV	Oral thrush,Difficulty swallowing	Patient reluctant initially but consented after health education.	\N	Kigali	Busanza	ACCEPTED	\N	\N	\N	\N
35340b74-bb21-41dd-95e1-c27d7531a959	PT-DE39650B	Pierre Hakizimana	1985-11-02	MALE	\N	+250788333444	f	TB	\N	\N	Amahoro Stadium area, Plot 88	Amahoro	Remera	Gasabo	1dd0bb68-cd37-4945-846c-8309613c438a	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1030	SYNCED	t	2026-06-10 17:16:24.023448	2026-06-10 19:25:06.342404	\N	CHW_SCREENING	PROVISIONAL	REF-2026-REM-7485	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-10 17:16:24.023236	\N	\N	TB	Persistent cough,Night sweats,Weight loss	Reports cough lasting over 4 weeks with night sweats. Awaiting clinical confirmation.	\N	Kigali	Nyabisindu	ACCEPTED	\N	\N	\N	\N
bfb73641-679c-4161-8587-b48754d8ea9b	PT-5A587173	Pierre Hakizimana	1999-06-05	Male	11999800987645	+250788000005	t	TB	\N	2026-06-12	Blue gate house near primary school	Gatsibo	Niboye	Kicukiro	02a4d824-e859-45e5-9092-40dc69fa4c89	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1022	PENDING	t	2026-06-05 12:16:53.236303	2026-06-19 05:41:33.755067	e93c9287-a7a8-4769-8076-086e7a36545d	CHW_SCREENING	CONFIRMED	REF-2026-NIB-8934	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-05 12:16:53.235442	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-19 05:41:32.658226	TB	string	coughing	\N	Kigali	Nyakabanda	ACCEPTED	\N	\N	\N	\N
6b2a9c4a-9845-406f-bf96-5e92606e3309	PT-B40E538E	Immaculee Nyiraneza	1978-08-14	FEMALE	1197808012345672	+250788555666	t	TB	\N	2026-04-11	Amahoro Stadium area, Plot 45	Amahoro	Remera	Gasabo	1dd0bb68-cd37-4945-846c-8309613c438a	c40666ca-46c3-45d7-a3b1-e25faa8f0126	1031	SYNCED	t	2026-06-10 17:16:25.520402	2026-06-10 19:25:06.342504	8eafea8b-ea87-4458-abc6-d2a3192b3461	CHW_SCREENING	ACTIVE	REF-2026-REM-6981	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-10 17:16:25.52005	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-10 17:16:26.867993	TB	Persistent cough,Fever	Household contact of confirmed TB patient. Sputum test positive.	GeneXpert: MTB detected, rifampicin sensitive. Sputum smear positive.	Kigali	Nyabisindu	ACCEPTED	\N	\N	\N	\N
49ca4358-c32e-44ba-bc52-92d731ea8f5b	PT-BD5C2489	Marie Uwimana	1990-05-20	FEMALE	1199050012345671	+250788111222	t	HIV	2025-12-12	\N	Near Amahoro Stadium, Plot 12	Amahoro	Remera	Gasabo	1dd0bb68-cd37-4945-846c-8309613c438a	c40666ca-46c3-45d7-a3b1-e25faa8f0126	\N	PENDING	t	2026-06-10 17:16:13.117945	2026-06-10 19:25:06.342305	760a3d01-20fa-4f17-8889-36305ef433aa	CHW_SCREENING	ACTIVE	REF-2026-REM-4520	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-10 17:16:13.024933	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-10 17:16:15.0171	HIV	Fatigue	Routine community follow-up screening.	CD4 count: 520 cells/mm3. HIV rapid test: reactive, confirmed by ELISA. Viral load suppressed.	Kigali	Nyabisindu	ACCEPTED	\N	\N	\N	\N
dce2bd61-cee2-4aac-a559-d537bbd6d175	PT-F952B170	Eric Mugisha	1992-03-15	MALE	1199203012345699	+250788777999	t	HIV	2026-06-01	\N	Near Amahoro Stadium, Plot 30	Amahoro	Remera	Gasabo	1dd0bb68-cd37-4945-846c-8309613c438a	c40666ca-46c3-45d7-a3b1-e25faa8f0126	\N	PENDING	t	2026-06-11 18:57:44.357974	2026-06-11 20:05:37.091128	d1d6dd86-79c5-4b85-a20b-7f17483ccfde	CHW_SCREENING	ACTIVE	REF-2026-REM-3332	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-11 18:57:44.356954	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-11 18:57:45.557996	HIV	Fatigue,Weight loss	Routine community follow-up screening for FHIR demo.	CD4 count: 480 cells/mm3. HIV rapid test: reactive, confirmed by ELISA.	Kigali	Nyabisindu	ACCEPTED	\N	\N	\N	\N
9c3bfdce-a0a2-4d3c-8c00-41988527b119	PT-24E38433	SMOKETEST Jean Mukiza	1990-05-12	MALE	1199080012345678	0788000111	t	HIV	2026-06-14	\N	\N	Kimisagara	Nyamirambo	Nyarugenge	1dd0bb68-cd37-4945-846c-8309613c438a	c40666ca-46c3-45d7-a3b1-e25faa8f0126	\N	PENDING	t	2026-06-14 21:35:07.608028	2026-06-14 21:41:03.308934	2d267ac3-e651-4eb3-b12d-282bbf8496a9	CHW_SCREENING	ACTIVE	REF-2026-NYA-6004	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-14 21:35:07.607157	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-14 21:41:02.309957	HIV	weight loss,fatigue	Smoke test patient - safe to delete	Smoke test - lab confirmed HIV positive	Kigali City	Cyivugiza	ACCEPTED	\N	\N	\N	\N
8009c775-9249-45bf-8f23-5270418f75d1	PT-D719B939	Jeanne Mukamana	1990-04-12	FEMALE	\N	0788123456	t	HIV	2026-06-15	\N	\N	Nyabisindu	Kimironko	Gasabo	1dd0bb68-cd37-4945-846c-8309613c438a	c40666ca-46c3-45d7-a3b1-e25faa8f0126	\N	PENDING	t	2026-06-15 08:41:18.238054	2026-06-15 08:43:54.943306	3870dcd3-2a10-41e7-893e-c3dc2c6c88df	CHW_SCREENING	ACTIVE	REF-2026-KIM-6935	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-15 08:41:18.237083	d40c82fe-c3f5-4105-be8a-b95b7308077f	2026-06-15 08:43:54.036697	HIV	\N	Smoke test re-run after credential-return fix	Lab confirmed HIV positive - smoke test	Kigali	Kibagabaga	ACCEPTED	\N	\N	\N	\N
\.


--
-- Data for Name: referrals; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.referrals (id, patient_id, referred_by_chw_id, confirmed_by_provider_id, referral_date, referral_reason, urgency, status, facility_appointment_date, provider_notes, attendance_notes, created_at, updated_at) FROM stdin;
b55e3fe9-5933-4082-9759-7c6dd96e8f50	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	56f20695-337b-42d9-becf-f7c675724aa6	2026-05-30	medication resistance, have rashes	ROUTINE	CONFIRMED	2026-06-01	\N	\N	2026-05-30 21:47:06.616986	2026-05-30 21:50:05.218853
aec9c377-34f9-4b32-b2c4-eaf8b9695a4c	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	56f20695-337b-42d9-becf-f7c675724aa6	2026-06-18	drug resistance	ROUTINE	CONFIRMED	2026-06-21	\N	\N	2026-06-18 10:45:34.242292	2026-06-18 23:05:30.108537
\.


--
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.refresh_tokens (id, user_id, token, expires_at, is_revoked, created_at) FROM stdin;
8185a3e3-5fe7-494f-b730-ebd5afa17fe5	3b8dafc7-ab88-49a0-8699-686d991df1ff	ce8c5702-b205-409e-a541-45e220e67a04	2026-06-06 16:31:28.313049	f	2026-05-30 16:31:28.409436
6400b618-e8b8-4d55-8f4e-aef3561727cf	3b8dafc7-ab88-49a0-8699-686d991df1ff	d49867fb-0933-4df3-b2dc-00e6a2506733	2026-06-06 16:41:46.214588	f	2026-05-30 16:41:46.214968
817bca85-794e-424b-abe9-278dbd53067d	4756ff76-d053-442f-810f-361073e5a6e1	45514263-7b9a-4b11-9a70-f71a631f75cb	2026-06-06 16:47:00.930687	f	2026-05-30 16:47:00.931049
7918c6bb-2cea-4717-b992-f059715701da	3b8dafc7-ab88-49a0-8699-686d991df1ff	d92501e3-6885-4443-9c56-23818fae3c87	2026-06-06 16:49:49.010197	f	2026-05-30 16:49:49.010501
86a29376-5d4e-4c13-af08-89c282d8dd3f	97375343-31d6-4817-8ac0-7542d847fc49	53256898-75dd-4cfb-9fbd-75ac753b735e	2026-06-06 17:34:50.8619	f	2026-05-30 17:34:50.961612
92c321c2-5bb1-4924-8e0c-909e1189c794	4756ff76-d053-442f-810f-361073e5a6e1	8bc37512-405e-4a68-b38e-a2f3453f0ece	2026-06-06 17:36:10.49478	f	2026-05-30 17:36:10.495121
1e39e4de-e530-405e-9142-a4b3388cf768	97375343-31d6-4817-8ac0-7542d847fc49	e8d79262-f377-4b2f-9037-6483dc6bfa37	2026-06-06 17:36:53.971936	f	2026-05-30 17:36:53.972324
54a9f100-1f04-4e5c-8276-a2fa25dd785e	3b8dafc7-ab88-49a0-8699-686d991df1ff	b39a2fb4-56d1-4e1f-b746-0e354003f122	2026-06-06 17:39:22.778066	f	2026-05-30 17:39:22.860867
1102c36c-c664-414a-a7ff-ad0647fd0b06	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	6819b24b-f8bc-4c07-9b44-0aede9266f37	2026-06-06 17:44:19.475019	f	2026-05-30 17:44:19.475411
0e02e589-1df6-4a89-bdaf-95b42270fd86	d40c82fe-c3f5-4105-be8a-b95b7308077f	6903fd59-0547-4dd5-95af-778d05f6bfaf	2026-06-06 18:00:41.961761	f	2026-05-30 18:00:41.962115
5693831b-273d-42ef-928d-e74ff32c4cbd	4756ff76-d053-442f-810f-361073e5a6e1	ae0b7980-7788-4644-9e1b-d18cebef057f	2026-06-06 18:02:03.861562	f	2026-05-30 18:02:03.861893
f2ed9cef-af6f-4592-8bc9-da5b31309d72	3b8dafc7-ab88-49a0-8699-686d991df1ff	cbc45328-92af-4675-b17f-ca699f028a00	2026-06-06 18:29:44.861442	f	2026-05-30 18:29:44.962261
9d71363f-62d8-4b58-8585-c8d6ed932490	4756ff76-d053-442f-810f-361073e5a6e1	cf72c0c5-39ee-4bbb-90b9-485caef4d414	2026-06-06 18:33:15.060783	f	2026-05-30 18:33:15.061242
56dd79f0-b230-4093-9141-110f911a0b43	97375343-31d6-4817-8ac0-7542d847fc49	54f30272-10bc-4c38-8568-b43cac570583	2026-06-06 18:35:33.467966	f	2026-05-30 18:35:33.468295
65f111ef-54f0-4423-9a1a-f5e1b39d8760	d40c82fe-c3f5-4105-be8a-b95b7308077f	19a49a64-0879-4c70-a095-7e67ea59fb52	2026-06-06 18:36:30.771213	f	2026-05-30 18:36:30.771563
fa8be191-f52b-4eb7-8e79-bc00b91330c4	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	ef7043a3-1c59-4019-ab07-e1d8897ac936	2026-06-06 18:38:02.466263	f	2026-05-30 18:38:02.466619
4096684b-363c-4977-8900-b7f596ba83fc	3b8dafc7-ab88-49a0-8699-686d991df1ff	5850a246-1c15-4a3e-a081-e563747cccc1	2026-06-06 21:37:21.217247	f	2026-05-30 21:37:21.315978
99bbd033-1115-4014-9510-53af5a2abccf	4756ff76-d053-442f-810f-361073e5a6e1	656bde29-a321-4b20-a2cd-161cb0a20d12	2026-06-06 21:39:19.523675	f	2026-05-30 21:39:19.524052
63327beb-28c0-4544-a77e-4c9b9bdf1acf	3b8dafc7-ab88-49a0-8699-686d991df1ff	4b6f9ba4-0c37-4a09-a474-2cd3c5404330	2026-06-06 21:44:24.41466	f	2026-05-30 21:44:24.414998
19a22d44-7928-40dd-a16f-3bd949f591ed	4756ff76-d053-442f-810f-361073e5a6e1	36db859c-4220-43a8-a669-3e49e68cca0e	2026-06-06 21:45:12.517091	f	2026-05-30 21:45:12.517486
058b0a60-16ca-4e5c-9e2b-54bb854b99ce	d40c82fe-c3f5-4105-be8a-b95b7308077f	96667439-f0d7-4a3f-8a18-20130141265e	2026-06-06 21:48:02.911044	f	2026-05-30 21:48:02.911494
9d600d41-895e-48a2-a64e-7c4477f612ea	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	eee562c0-6d80-427b-80f0-75724c058482	2026-06-06 21:51:23.918458	f	2026-05-30 21:51:23.918769
29c825c8-ee35-4306-ae09-0ed0df85cb82	4756ff76-d053-442f-810f-361073e5a6e1	95ae576a-22a7-428f-a0ec-41eb4b851633	2026-06-06 21:53:00.630426	f	2026-05-30 21:53:00.63079
900b3c92-587f-49d6-a209-67cde8f6259e	3414701f-3738-4f15-8e0e-e84cd8692388	b8ab7d26-26a5-4942-847e-c1f73d05ef12	2026-06-06 21:57:40.018956	f	2026-05-30 21:57:40.0193
2a49669c-24b9-47a8-a377-86e4e7b1bac6	4756ff76-d053-442f-810f-361073e5a6e1	87fd5b0b-28c4-429b-bae4-be6d7d9a13ad	2026-06-09 16:52:42.604517	f	2026-06-02 16:52:42.705688
cd96ae97-7f82-447e-8656-be2177baa8f1	4756ff76-d053-442f-810f-361073e5a6e1	46f505bd-1149-49fb-8076-bfac235b4936	2026-06-09 16:56:33.610125	f	2026-06-02 16:56:33.610537
59eaccf0-e41e-4f2a-9d94-b353dd4aede9	3b8dafc7-ab88-49a0-8699-686d991df1ff	2e8131af-21e1-4f66-854b-0fcabf039a1a	2026-06-09 16:57:17.100669	f	2026-06-02 16:57:17.101106
6c1dc6a9-52a4-42c3-be6b-8b6e9e73df6d	3414701f-3738-4f15-8e0e-e84cd8692388	7e11ba6c-51b6-42ef-841f-5fc58f9a4884	2026-06-09 17:00:17.108802	f	2026-06-02 17:00:17.109551
d4762c57-4b02-4319-ad3e-9a10e91bf9f9	3b8dafc7-ab88-49a0-8699-686d991df1ff	c2644636-2393-4199-bfdb-aa2ce707145b	2026-06-09 17:02:29.313444	f	2026-06-02 17:02:29.31375
582e1975-b18f-4788-ab0e-68ab52ad9e1b	3b8dafc7-ab88-49a0-8699-686d991df1ff	b4fea51e-f2d4-4fe0-9c52-3b28792b65f8	2026-06-09 17:04:19.904951	f	2026-06-02 17:04:19.905317
0569bf72-cb4e-46fb-afe6-4328cb1d4a14	4756ff76-d053-442f-810f-361073e5a6e1	4cae4a3b-b4c1-4a56-920a-6d33ae1c9af7	2026-06-09 17:14:07.90051	f	2026-06-02 17:14:07.900984
66bb17ca-a8ae-4ba5-b2f8-fcfbbb226d96	4756ff76-d053-442f-810f-361073e5a6e1	70f3717e-3bdb-4d8b-b56f-1e5a53a9f771	2026-06-09 17:58:43.62706	f	2026-06-02 17:58:43.725271
63ce57e6-54cf-4e78-a247-54719137c29d	3b8dafc7-ab88-49a0-8699-686d991df1ff	3808312e-2a14-41c2-99df-03bd2964ba9a	2026-06-09 17:59:52.726825	f	2026-06-02 17:59:52.727183
05a232c3-a38f-4f0e-8498-82c5c237edb3	4756ff76-d053-442f-810f-361073e5a6e1	eb5e7038-ec3f-4fb9-b7ce-59088c7289f1	2026-06-10 08:16:14.162642	f	2026-06-03 08:16:14.259327
43dc24e2-2a9f-4473-a613-efd344c0d46e	4756ff76-d053-442f-810f-361073e5a6e1	15eca1ff-713d-4777-87cb-0910aae352a7	2026-06-10 08:20:17.759659	f	2026-06-03 08:20:17.760014
6e19b24a-a5c7-47b8-ae22-6e5446cffb1b	4756ff76-d053-442f-810f-361073e5a6e1	1387491b-f258-4e45-876a-f68796694be8	2026-06-10 10:36:11.381075	f	2026-06-03 10:36:11.577082
580ff75a-629f-47f6-9740-0d0a451220b3	3b8dafc7-ab88-49a0-8699-686d991df1ff	fd031574-73b1-4e1b-aa3d-aa8918f99796	2026-06-12 09:57:27.817074	f	2026-06-05 09:57:27.914074
8ac7d7dc-3a28-4c9c-8334-5aacef2409fa	3b8dafc7-ab88-49a0-8699-686d991df1ff	84b6c002-5092-4b6b-aa51-5513b0f801f5	2026-06-12 09:58:27.718496	f	2026-06-05 09:58:27.718917
2982ba31-6f73-4f4c-ac92-d04ab1cbc432	3b8dafc7-ab88-49a0-8699-686d991df1ff	77cdc111-79b8-45af-a02b-ff518fd497f5	2026-06-12 11:30:06.67918	f	2026-06-05 11:30:06.68607
8ebc24f5-f89a-4c40-9fac-2d446291ef79	786e213d-d2bd-4541-883f-73e3b51e7da0	5a847caf-75e9-4356-a6f2-9bbb4cbe502b	2026-06-12 11:37:43.984949	f	2026-06-05 11:37:43.985243
74f1bf67-fefb-4a20-a3f9-89d830ac868d	4756ff76-d053-442f-810f-361073e5a6e1	8847aa6e-4236-484f-9e46-1fe76da76694	2026-06-12 12:09:55.93881	f	2026-06-05 12:09:56.037153
158e937c-e689-4080-927c-6fe6eb7b7a39	4756ff76-d053-442f-810f-361073e5a6e1	0e1f8939-2860-4f47-9aca-7fbf41376c75	2026-06-13 17:01:50.095209	f	2026-06-06 17:01:50.100874
85dc77d4-aa24-4633-8d16-01f71764f05e	4756ff76-d053-442f-810f-361073e5a6e1	14b28d59-3687-4f46-845e-dae76e8b2488	2026-06-13 17:06:05.595843	f	2026-06-06 17:06:05.596285
9d426318-cd1d-4b54-a562-f757283f8773	d40c82fe-c3f5-4105-be8a-b95b7308077f	3431a4b6-3348-42af-a896-97ba803d9d6c	2026-06-13 17:21:42.204953	f	2026-06-06 17:21:42.205364
065d5b10-4398-4984-b3e4-19167a60aec1	3b8dafc7-ab88-49a0-8699-686d991df1ff	d38d68a5-a8e8-4223-a052-f86889743166	2026-06-16 07:50:06.461654	f	2026-06-09 07:50:06.560248
98eaac1d-5e3d-4f0d-a1cf-cf45141db71d	4756ff76-d053-442f-810f-361073e5a6e1	a5425cac-07af-41b0-b543-b99f3e9a3614	2026-06-16 07:55:41.356531	f	2026-06-09 07:55:41.356991
2d68b298-bda3-4708-96c4-3ae3eebb5350	d40c82fe-c3f5-4105-be8a-b95b7308077f	7459b960-874a-45f4-977e-95e0a430ae80	2026-06-16 07:58:29.167407	f	2026-06-09 07:58:29.167848
f87027b4-6b0a-41ad-8c74-f1360d61d6e6	4756ff76-d053-442f-810f-361073e5a6e1	c0382013-1055-443e-a390-0e96c09f265a	2026-06-16 08:11:42.465173	f	2026-06-09 08:11:42.465751
868d3394-13f7-4100-ab69-8e0f9600d772	d40c82fe-c3f5-4105-be8a-b95b7308077f	a108da87-99ab-4d0d-bc34-c0c867043591	2026-06-16 08:13:43.464676	f	2026-06-09 08:13:43.464974
bc4aa7b7-e62b-4bd8-b4e8-8ed305c80758	4756ff76-d053-442f-810f-361073e5a6e1	8c0f7238-cb29-4eda-b9bf-b2fa843a6507	2026-06-16 08:43:11.304952	f	2026-06-09 08:43:11.40468
8240f2e3-0b41-4e88-8396-ea4d8e9f7921	d40c82fe-c3f5-4105-be8a-b95b7308077f	1dd1e01e-c6d1-4317-9c62-57caffa3473a	2026-06-16 08:53:22.01805	f	2026-06-09 08:53:22.018383
1ff6cbe6-06cc-4a79-ae73-0a3921908b83	4756ff76-d053-442f-810f-361073e5a6e1	bffc0e50-c01d-4eeb-89ae-648e7598f2c3	2026-06-16 08:56:09.604607	f	2026-06-09 08:56:09.604996
c3f92735-f99f-4c03-b32e-9a04c0ec5c75	d40c82fe-c3f5-4105-be8a-b95b7308077f	483bf965-3538-4eab-8cac-35286e78eda4	2026-06-16 09:01:20.812634	f	2026-06-09 09:01:20.812969
158a64f6-43ba-4d4a-9b54-ed7e7ff44388	3b8dafc7-ab88-49a0-8699-686d991df1ff	6526c388-d5c9-4bd4-beb5-d98b6d50a1ae	2026-06-16 09:29:48.414604	f	2026-06-09 09:29:48.513404
e480d576-9b48-41d5-a3f8-6849255ef09c	4756ff76-d053-442f-810f-361073e5a6e1	92424b1a-24cf-4689-a224-55ad9d6cf5cd	2026-06-16 09:55:44.809891	f	2026-06-09 09:55:44.810318
8fcbb5a2-6115-4f35-8656-8fe6dc191ef7	3b8dafc7-ab88-49a0-8699-686d991df1ff	185c9af4-0e9c-46eb-951b-ef8e4d0d5276	2026-06-17 08:59:43.110208	f	2026-06-10 08:59:43.308649
a39a2279-d002-4f34-88e7-308511949ba5	97375343-31d6-4817-8ac0-7542d847fc49	ba8713d2-3741-4bd4-a24d-dba55c424ec1	2026-06-17 09:02:49.814193	f	2026-06-10 09:02:49.814584
d299cb6f-590f-429c-b3e0-ffebe33fa3c3	d40c82fe-c3f5-4105-be8a-b95b7308077f	40a37a39-fd5c-41f2-aa4c-a2fa18346a57	2026-06-17 09:07:50.916339	f	2026-06-10 09:07:50.916671
553f1679-48c6-4b10-8817-d1eee7178bf6	d40c82fe-c3f5-4105-be8a-b95b7308077f	a8f97cce-0b54-49c4-ad29-9f76bcf39e98	2026-06-17 10:05:23.043226	f	2026-06-10 10:05:23.141192
7f06fba1-6a59-423a-8d49-f17fa55676f3	d40c82fe-c3f5-4105-be8a-b95b7308077f	765df4d1-772b-466a-9c71-c9099a94b71f	2026-06-17 10:23:18.743665	f	2026-06-10 10:23:18.841541
bc0afa35-12d6-43f7-a709-924e0093c097	d40c82fe-c3f5-4105-be8a-b95b7308077f	2ba4dfcd-393e-4ef6-93f7-4584a37417ab	2026-06-17 10:50:37.818903	f	2026-06-10 10:50:37.917953
defd036a-f061-4440-9669-7f87628b3852	3b8dafc7-ab88-49a0-8699-686d991df1ff	1931082d-a458-410b-8fd0-8b20f681459f	2026-06-17 14:23:33.873285	f	2026-06-10 14:23:33.974064
7fa921c4-7e68-44c8-9166-4533d189ca18	b23bc14f-1785-4679-bdbe-81f0c1b779f2	17c30b22-61e5-4bc0-b195-d86ee022b26e	2026-06-17 14:28:03.873937	f	2026-06-10 14:28:03.874309
1f71f601-f6d2-4a8c-bfa3-b03fce78b65e	97375343-31d6-4817-8ac0-7542d847fc49	724b6bec-1cc5-4d7b-9068-408f1f05feed	2026-06-17 14:33:34.577626	f	2026-06-10 14:33:34.577931
731b656c-1a38-48be-9096-fffe6c51d8db	97375343-31d6-4817-8ac0-7542d847fc49	b5021cbe-8b62-4c75-986b-455a557daf34	2026-06-17 15:18:57.378853	f	2026-06-10 15:18:57.474214
846e3534-55b2-49bf-ad8f-bffa683b1dd1	97375343-31d6-4817-8ac0-7542d847fc49	a991a0b7-bd32-4285-8f02-6bd057d10d83	2026-06-17 15:48:58.761067	f	2026-06-10 15:48:58.868152
1a212488-bebc-4586-9d9c-e8c3954be283	3b8dafc7-ab88-49a0-8699-686d991df1ff	c6684fbc-907a-46c2-9894-919d98d25817	2026-06-17 16:42:37.993588	f	2026-06-10 16:42:38.093438
ac9ff81a-c57c-4286-98ce-e87c07100905	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	ce4c2cf1-0434-4301-99a3-120ffff3b9f4	2026-06-17 16:46:20.587389	f	2026-06-10 16:46:20.587768
c5897885-d87a-477b-9f64-7571ce93d08f	97375343-31d6-4817-8ac0-7542d847fc49	c94885c4-a943-46fb-9824-712bcb261a1a	2026-06-17 17:15:27.321327	f	2026-06-10 17:15:27.420307
f69c0b17-4b4e-4ede-beb6-a1866754e145	97375343-31d6-4817-8ac0-7542d847fc49	00d60e95-6e59-469a-8a9a-4de28f6a317c	2026-06-17 17:15:45.025059	f	2026-06-10 17:15:45.025583
c547bfc5-483b-4ba3-ab46-800b9524e7fe	97375343-31d6-4817-8ac0-7542d847fc49	25c1b7a6-f1c6-4f31-85aa-7a9279a66370	2026-06-17 17:16:09.130666	f	2026-06-10 17:16:09.131046
afdcb6d3-ec60-4ab2-9d4a-b150c23bf90a	d40c82fe-c3f5-4105-be8a-b95b7308077f	9eca19c1-c953-4111-bdf7-139a5b5d9807	2026-06-17 17:16:10.53469	f	2026-06-10 17:16:10.535046
dbb54b32-7f9c-4106-8487-9df46250c30a	97375343-31d6-4817-8ac0-7542d847fc49	dc81e37f-72f0-48b9-a35e-fd1a7fe33d97	2026-06-17 18:44:37.43927	f	2026-06-10 18:44:37.443985
feee47f1-aa42-41b0-bba5-e296f7580b02	3b8dafc7-ab88-49a0-8699-686d991df1ff	04249248-82db-42ce-9e2d-7ffe659b60c3	2026-06-17 19:23:51.346425	f	2026-06-10 19:23:51.447733
8c16b945-fbf0-4aad-826e-6cdbeb6936ca	3b8dafc7-ab88-49a0-8699-686d991df1ff	ce2ae8e4-1d90-4d60-8b3a-0187ceab56fd	2026-06-17 19:25:03.046195	f	2026-06-10 19:25:03.046703
df62a9d1-506b-45ac-ab92-04d82cbb820b	3b8dafc7-ab88-49a0-8699-686d991df1ff	738250db-d88e-46eb-b4d9-ff74b060c5ef	2026-06-17 19:25:15.547724	f	2026-06-10 19:25:15.548096
7fd18130-097b-4985-bef9-29a396a6a3a5	97375343-31d6-4817-8ac0-7542d847fc49	9df33ec5-87f4-4ff9-828c-2c8e53e42f7f	2026-06-17 19:30:50.630786	f	2026-06-10 19:30:50.631151
4ce3fd53-2a61-4fbe-ab90-4283eb7f64c3	97375343-31d6-4817-8ac0-7542d847fc49	b491abcf-a121-4de7-a2ee-344e378116f2	2026-06-17 19:37:51.651085	f	2026-06-10 19:37:51.651429
fb538571-f0fe-4a71-8134-b4fd221ed40c	d40c82fe-c3f5-4105-be8a-b95b7308077f	fd58a0a7-e76d-4f13-a5c6-9a256e83d800	2026-06-17 19:42:14.246511	f	2026-06-10 19:42:14.24681
528859c8-c558-45b6-8e23-ffbad90ff3e7	d40c82fe-c3f5-4105-be8a-b95b7308077f	5d78c2ab-679d-41d6-b5c0-af063a79eaf4	2026-06-17 19:42:39.949389	f	2026-06-10 19:42:39.949829
90bb7351-6bab-46e2-aa07-82d7fe012bcd	d40c82fe-c3f5-4105-be8a-b95b7308077f	7bf20960-4682-44b0-9739-b2dd22c64d08	2026-06-17 19:44:04.844304	f	2026-06-10 19:44:04.844681
6d0ac588-af0b-4bde-87bc-b33d46981623	d40c82fe-c3f5-4105-be8a-b95b7308077f	dcf46ccd-7c4b-43b0-b04f-0a600dde7208	2026-06-17 19:46:30.531025	f	2026-06-10 19:46:30.531291
c2049872-7c9e-4446-9de4-a1f6eea0b5dc	97375343-31d6-4817-8ac0-7542d847fc49	8849ff75-3cbd-4f75-9ccd-654549ff5d38	2026-06-17 20:08:30.915288	f	2026-06-10 20:08:31.017277
ec4b7b70-2655-4875-9454-e28019efc2f9	97375343-31d6-4817-8ac0-7542d847fc49	9a20a9a0-51cc-4a18-a559-3d447c9b1584	2026-06-17 20:09:58.722091	f	2026-06-10 20:09:58.722527
b625ea6e-6fbf-4734-a5e0-c372a6d0f8f0	3b8dafc7-ab88-49a0-8699-686d991df1ff	c17019b8-caba-4a43-9195-0b8ab3a1f091	2026-06-17 20:57:23.838125	f	2026-06-10 20:57:23.93494
07ec1b7d-835c-4c8f-8de5-94556144c7c8	3b8dafc7-ab88-49a0-8699-686d991df1ff	68ebb024-f73a-4187-9c23-76c43fc4ade8	2026-06-17 20:57:50.242331	f	2026-06-10 20:57:50.242672
95bd208c-eaed-4fcb-8648-624f59761af9	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	aa175624-147a-437e-8bd6-2b473e8a8be0	2026-06-17 20:58:06.642864	f	2026-06-10 20:58:06.643229
5b61829b-ae08-4e61-ad56-4355f4260ea3	3b8dafc7-ab88-49a0-8699-686d991df1ff	04270347-4daf-4378-b021-9f362754a827	2026-06-17 20:59:35.340483	f	2026-06-10 20:59:35.340839
a2499c0d-922e-4dbb-a41e-fe807a31534a	97375343-31d6-4817-8ac0-7542d847fc49	4171f681-9c67-4a27-b695-46b5c4e9f90e	2026-06-17 20:59:36.635812	f	2026-06-10 20:59:36.636152
7d49457f-9fd3-4fb5-a9d9-5e1e1eee4191	d40c82fe-c3f5-4105-be8a-b95b7308077f	75c671d9-e483-4ea0-89c1-f3d555ebfcdd	2026-06-17 20:59:38.632975	f	2026-06-10 20:59:38.633342
c0755be0-0fde-41e9-8138-7770337474ba	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	612975e8-fee2-4fa7-96ce-6a11973baedb	2026-06-17 20:59:40.538879	f	2026-06-10 20:59:40.539176
22d9fe6e-f95a-4870-8acd-4909cd8f0e26	3b8dafc7-ab88-49a0-8699-686d991df1ff	23238765-a47a-4cbf-9746-045c68d300b0	2026-06-17 22:08:58.928126	f	2026-06-10 22:08:59.027952
23185b79-9e39-4cd9-8255-ab30a786ec35	3b8dafc7-ab88-49a0-8699-686d991df1ff	3f3d7cd6-7e88-4b48-9f12-9f219676f1c7	2026-06-17 22:53:39.001436	f	2026-06-10 22:53:39.192131
f8bd8938-64f0-4837-9ed4-af09332fb86a	3b8dafc7-ab88-49a0-8699-686d991df1ff	af5adbe5-5f4b-4559-ad66-d6edf68bd7c9	2026-06-12 10:03:24.628328	t	2026-06-05 10:03:24.711399
f292cf50-7dc7-4c07-b8ea-e0787d70cac8	3b8dafc7-ab88-49a0-8699-686d991df1ff	0276be77-855d-43a1-b776-b9f48d2c730e	2026-06-18 08:44:29.444727	f	2026-06-11 08:44:29.445754
1be51b87-0aa6-4f3a-ab4e-8a399909d5a8	3b8dafc7-ab88-49a0-8699-686d991df1ff	c38fe349-33f3-4d3b-9c63-390642c595b9	2026-06-18 09:44:18.176855	f	2026-06-11 09:44:18.372823
e1595146-3761-40f0-bb5f-b73dcddc6b0b	4756ff76-d053-442f-810f-361073e5a6e1	63dc6ada-09c0-4482-aea5-c6260fb2b84c	2026-06-18 16:49:05.542489	f	2026-06-11 16:49:05.64158
51739c5b-3639-43d3-9e36-14caa2e9d82e	97375343-31d6-4817-8ac0-7542d847fc49	2b28a54c-69ed-4a9a-af4d-f9ba07cfc942	2026-06-18 16:50:16.245293	f	2026-06-11 16:50:16.24562
c6aba211-0ca2-45da-b748-daeef9d08ac8	d40c82fe-c3f5-4105-be8a-b95b7308077f	90213f28-7681-42ce-a573-ca2125224fab	2026-06-18 16:51:47.847976	f	2026-06-11 16:51:47.848307
84e5e8d1-830b-431b-96a8-b0c34fa45bb3	4756ff76-d053-442f-810f-361073e5a6e1	601ab6cc-fcfd-4ddc-8b1e-b1b32a12412a	2026-06-18 16:55:35.643834	f	2026-06-11 16:55:35.644147
5ad7ed73-54d3-4bd6-afe6-fa88774280b1	3b8dafc7-ab88-49a0-8699-686d991df1ff	b85d2daa-f229-422c-affe-89022167ea96	2026-06-18 17:17:03.754468	f	2026-06-11 17:17:03.754817
c57fab8f-d0a1-4e45-8b6a-9d16ffa6ecfe	3b8dafc7-ab88-49a0-8699-686d991df1ff	c08253db-a25e-4fe3-9629-e794dfaaa4a9	2026-06-18 18:35:38.664976	f	2026-06-11 18:35:38.761319
07b35de9-76cf-45ca-99f4-424ad9f60652	3b8dafc7-ab88-49a0-8699-686d991df1ff	9ecb046d-e749-4dd2-a129-d18077ca2b83	2026-06-18 18:35:38.663408	f	2026-06-11 18:35:38.765928
e04af2bb-05e8-43a5-9444-614a5b5be0ea	3b8dafc7-ab88-49a0-8699-686d991df1ff	c9e5ddca-d733-4af8-aa18-3b677a4f4bf3	2026-06-18 18:36:26.66039	f	2026-06-11 18:36:26.660727
51a8ecba-a9e3-4d27-9848-ffa06ad39e93	97375343-31d6-4817-8ac0-7542d847fc49	467051ae-d9c6-47e8-9647-272fd7727a96	2026-06-18 18:57:41.966241	f	2026-06-11 18:57:41.966519
4c321775-2865-44cb-bb47-9e530ef52d44	d40c82fe-c3f5-4105-be8a-b95b7308077f	98b0af65-f1a3-4e62-ac0c-53f9893fd414	2026-06-18 18:57:43.26727	f	2026-06-11 18:57:43.267528
154a79eb-46e0-4745-bb27-3608a966ae7c	3b8dafc7-ab88-49a0-8699-686d991df1ff	e42e1075-1798-48ea-a3fa-ce44c0a49c05	2026-06-18 20:05:32.690388	f	2026-06-11 20:05:32.887874
58536fc0-f870-42d9-aff6-8cb2cd29881d	3b8dafc7-ab88-49a0-8699-686d991df1ff	fb38b0f4-532a-4ab1-a8bc-b4658fd1f351	2026-06-18 20:20:10.798814	f	2026-06-11 20:20:10.799221
0cc0a34d-cbc8-4d96-b4c8-842c528247f3	3b8dafc7-ab88-49a0-8699-686d991df1ff	09d93763-908c-4ea4-8c99-74763bbcd0de	2026-06-18 20:20:23.287803	f	2026-06-11 20:20:23.28828
c5e12c93-b232-41d5-9175-c9b4909071be	3b8dafc7-ab88-49a0-8699-686d991df1ff	fe157e86-b6c3-436f-9cf3-9d5dce84835d	2026-06-18 20:20:32.004948	f	2026-06-11 20:20:32.005334
bc67e404-0e05-4a9f-a44c-ea26a13ee95c	760a3d01-20fa-4f17-8889-36305ef433aa	ad4daaf0-3bd6-4a4e-b772-c18c490235cd	2026-06-18 20:53:24.361715	f	2026-06-11 20:53:24.362453
d49299c5-09ad-49b1-a061-928a8abcdd87	4756ff76-d053-442f-810f-361073e5a6e1	1e8976b7-9f4b-431e-8187-609144fe4e77	2026-06-18 21:00:23.659604	f	2026-06-11 21:00:23.659933
95446148-ec7d-4f23-92c9-0b342bed1438	d40c82fe-c3f5-4105-be8a-b95b7308077f	9a5d8762-a6cb-456b-8faa-7c47156b8957	2026-06-18 21:31:18.562119	f	2026-06-11 21:31:18.562589
9d0bd991-c1f5-44d6-b603-5ff6f8957c1b	3b8dafc7-ab88-49a0-8699-686d991df1ff	d183e334-ed6e-45c5-b49f-37992a78dbd5	2026-06-18 21:57:16.871497	f	2026-06-11 21:57:16.871755
5ffc0b66-562c-459c-a4a4-b5b685002c9e	3b8dafc7-ab88-49a0-8699-686d991df1ff	45770e28-c005-4210-b9eb-09dce3bf6307	2026-06-18 22:01:24.170849	f	2026-06-11 22:01:24.254734
5509b633-3984-41ba-95d8-cfc6d87c2ab6	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	fb3ede58-2a4f-4503-b49b-8b35534b7d53	2026-06-18 22:08:11.460809	f	2026-06-11 22:08:11.461017
daebfdcd-bce2-42db-9ca2-518cadb66286	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	e94a1675-6957-49f2-9c3d-7dbcea38881d	2026-06-18 22:33:06.460799	f	2026-06-11 22:33:06.465954
9f41ad31-4f93-48f3-a6e7-9fdc582ddc4c	d40c82fe-c3f5-4105-be8a-b95b7308077f	542431c9-b8e7-4411-ac71-4fc45a9f3ba4	2026-06-18 23:13:04.817973	f	2026-06-11 23:13:04.822878
48ebea27-d2d7-44f3-a636-1cbdffe7cd30	3b8dafc7-ab88-49a0-8699-686d991df1ff	69b0de23-cd98-46eb-85a8-dc4f7a4a0e54	2026-06-19 18:51:22.355145	f	2026-06-12 18:51:22.356311
7f7d63c1-4d04-41a9-9c5c-58854b0cdb55	97375343-31d6-4817-8ac0-7542d847fc49	0ba7509b-9270-4a4c-b050-f9d272481e6c	2026-06-21 12:22:46.342586	f	2026-06-14 12:22:46.342935
c72cd215-68a1-4445-b83d-ccf22c58dc85	97375343-31d6-4817-8ac0-7542d847fc49	ca191443-6e00-4c88-adf4-e2d06e472e6a	2026-06-21 12:22:46.442291	f	2026-06-14 12:22:46.44295
c0bf3a8d-32b9-4a4b-8a23-f22386a1981b	97375343-31d6-4817-8ac0-7542d847fc49	f2ad91c2-d52c-4f25-8720-8ed30a87e1ed	2026-06-21 12:22:46.342675	f	2026-06-14 12:22:46.342932
bed9f369-0d21-49c5-aa95-8f2505cb0f08	97375343-31d6-4817-8ac0-7542d847fc49	5a1dde51-c83a-4bf3-a592-47cf59e25fee	2026-06-21 12:22:46.342616	f	2026-06-14 12:22:46.342989
a14a3fd6-0e45-46a7-a6b7-e9d8836cde35	97375343-31d6-4817-8ac0-7542d847fc49	d36cd05c-2195-44aa-be73-613eec327cf7	2026-06-21 12:22:46.342587	f	2026-06-14 12:22:46.342949
9e3dc195-7f0b-4cc1-8849-958ee4f24a80	97375343-31d6-4817-8ac0-7542d847fc49	90314bd9-e4f5-4aeb-b6f5-afe927e20041	2026-06-18 21:06:23.36041	t	2026-06-11 21:06:23.360732
5359a091-af1f-4e1d-a44d-7ced91472f80	3b8dafc7-ab88-49a0-8699-686d991df1ff	4838dce2-d179-4d8c-9356-cf77ebff398c	2026-06-21 12:24:38.05359	f	2026-06-14 12:24:38.053932
3f0a0928-4310-4405-9dea-86d246893000	3b8dafc7-ab88-49a0-8699-686d991df1ff	2eaf0564-d960-4613-ae66-256435321354	2026-06-21 12:32:09.245019	f	2026-06-14 12:32:09.245314
4fde2b13-cf4c-4941-a38e-8e987163ae3d	3b8dafc7-ab88-49a0-8699-686d991df1ff	44f74ee4-812c-47ed-889e-95d7d97fb206	2026-06-21 12:28:32.452356	f	2026-06-14 12:28:32.45267
ea9d297f-1129-4fd5-a92a-5b845a638a36	3b8dafc7-ab88-49a0-8699-686d991df1ff	a34b76ff-b440-4f6d-851a-297ec9d09691	2026-06-21 12:43:18.649226	f	2026-06-14 12:43:18.64975
a3c94463-ac26-409d-a6f6-c5aff291b797	3b8dafc7-ab88-49a0-8699-686d991df1ff	44c60934-8350-49d4-817f-2f4e6fe9b0e4	2026-06-21 21:32:21.010737	f	2026-06-14 21:32:21.106228
b15460ca-c3fa-4255-9980-accf40e5153f	97375343-31d6-4817-8ac0-7542d847fc49	972b6017-3f05-450f-9c56-71648e0e26cb	2026-06-21 21:34:28.521046	f	2026-06-14 21:34:28.521431
069179b8-a77c-4bce-a1e1-d819db7ebd63	d40c82fe-c3f5-4105-be8a-b95b7308077f	41ba35f3-05fe-4211-ade9-8b34c0a79165	2026-06-21 21:40:39.712204	f	2026-06-14 21:40:39.712511
696bdf2f-c1eb-4bf6-b205-1ae72befd354	97375343-31d6-4817-8ac0-7542d847fc49	c9b42d13-e8f9-4900-81be-b5957c0e6a99	2026-06-22 08:40:13.539555	f	2026-06-15 08:40:13.640762
412896e2-5a47-4720-9205-6ba01b57ebbd	d40c82fe-c3f5-4105-be8a-b95b7308077f	6c9e18dc-7b2c-44b0-8f79-763e76d48400	2026-06-22 08:42:13.836424	f	2026-06-15 08:42:13.836955
f3c0e38c-d7d1-4658-b171-6a71a8b90c29	3870dcd3-2a10-41e7-893e-c3dc2c6c88df	f6f576c7-5968-47f3-9f32-bc209388b39a	2026-06-22 09:00:43.538418	f	2026-06-15 09:00:43.538807
87fdf695-c553-4554-a608-3d16af970b96	97375343-31d6-4817-8ac0-7542d847fc49	bb86bd64-975d-4e2b-b1ee-8794d24d3d4c	2026-06-22 09:05:48.738627	f	2026-06-15 09:05:48.739102
53ea9c0e-374a-4d3e-b097-6056e652041e	97375343-31d6-4817-8ac0-7542d847fc49	e97b5ec4-e1c4-49c6-a8c7-14c637a03c9e	2026-06-22 09:36:59.7958	f	2026-06-15 09:36:59.796524
16af0953-c716-493d-abbf-654825ecdbc1	d40c82fe-c3f5-4105-be8a-b95b7308077f	c1c6e3f2-a11b-4aeb-aef8-46eebe1fae80	2026-06-22 10:32:15.024576	f	2026-06-15 10:32:15.126906
9343f50e-a0a0-45fd-9075-8c384b09db07	3b8dafc7-ab88-49a0-8699-686d991df1ff	91768cad-23af-4340-8288-de7b79e1b022	2026-06-22 11:24:46.817435	f	2026-06-15 11:24:46.822827
b4be7c8f-559c-4c19-8e2a-2aeb7131d6b0	3b8dafc7-ab88-49a0-8699-686d991df1ff	0603db2a-6bde-4e42-aa79-43717183a1b5	2026-06-22 13:24:05.693563	f	2026-06-15 13:24:05.792505
11a1341c-c52d-44b7-b90b-b205ca7e323a	4756ff76-d053-442f-810f-361073e5a6e1	07c8e026-fab0-4cd1-ba57-b5e935f338d1	2026-06-22 18:37:33.158062	f	2026-06-15 18:37:33.259103
a8e23d40-0c8c-43b7-a7bb-d6e6e06ef677	3b8dafc7-ab88-49a0-8699-686d991df1ff	ab90ae2e-20b7-4052-90e7-6207baf87a7a	2026-06-22 18:40:50.629074	f	2026-06-15 18:40:50.629509
d99943ab-4283-4403-9fd6-7d422be7c578	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	913b3624-4fb9-43aa-a4bc-0271b2d39838	2026-06-22 19:26:49.218085	f	2026-06-15 19:26:49.31512
c10fd8d1-2890-4c00-a667-44134c1defb6	3b8dafc7-ab88-49a0-8699-686d991df1ff	f5fd0792-5d33-4c6e-adc8-a99ce3304af5	2026-06-22 19:49:30.818656	f	2026-06-15 19:49:30.818997
3ccb5269-46b0-4360-be61-03709f3a71cb	97375343-31d6-4817-8ac0-7542d847fc49	66cfb463-7fea-46d7-a8ee-3ca771f1edea	2026-06-22 19:52:20.630861	f	2026-06-15 19:52:20.631244
beb52b57-c9af-4461-a202-b0340cf4fb0a	3b8dafc7-ab88-49a0-8699-686d991df1ff	6c6c43c9-b6d0-4bed-85fb-e6887985a159	2026-06-22 20:01:28.827539	f	2026-06-15 20:01:28.827876
0e7aaa0e-c86c-452e-b000-924312f2cdba	97375343-31d6-4817-8ac0-7542d847fc49	dd26960d-d91f-46da-a3b7-03a61168cc2a	2026-06-22 20:22:08.329183	f	2026-06-15 20:22:08.329541
b38f6602-4995-40c1-9a63-5df33ecb0c82	97375343-31d6-4817-8ac0-7542d847fc49	af6cafd6-f050-43a2-9e8c-84db056de03b	2026-06-22 20:23:24.517439	f	2026-06-15 20:23:24.517765
a41a0bd7-3bc0-44df-a3d2-aadc6ed228d4	97375343-31d6-4817-8ac0-7542d847fc49	37b159d7-a9b9-4393-896a-0e8b22a95080	2026-06-22 20:57:24.351409	f	2026-06-15 20:57:24.450868
f263104f-bcb1-4272-a0d5-8e93860ab10f	97375343-31d6-4817-8ac0-7542d847fc49	f9b4fc6c-98aa-4402-ba5c-65cdbab0fae2	2026-06-22 20:57:24.351411	f	2026-06-15 20:57:24.450869
96b8c947-4153-462a-8ece-6f8bc6134afc	97375343-31d6-4817-8ac0-7542d847fc49	c0399d7f-d214-4d74-a898-a215e2dab82c	2026-06-22 20:57:24.35136	f	2026-06-15 20:57:24.450895
5026e870-1694-4fe9-b815-9568399086f0	97375343-31d6-4817-8ac0-7542d847fc49	180dde84-2e44-45c2-9be5-8f7eb295e9bc	2026-06-22 20:57:24.351335	f	2026-06-15 20:57:24.450915
5afee79f-7f9b-49d1-9cb1-16515fddc91e	97375343-31d6-4817-8ac0-7542d847fc49	e2d42ec3-cd8e-4a9b-8c43-f242d8df0bec	2026-06-22 20:57:24.351319	f	2026-06-15 20:57:24.45087
07fa4224-160d-4a6b-9209-ada26d1a9e8e	97375343-31d6-4817-8ac0-7542d847fc49	505081e2-ed3f-4ed9-be91-0fb5d4814e2e	2026-06-22 20:32:40.922145	t	2026-06-15 20:32:40.922425
990780e0-0280-478e-a6d0-e93fd7d8d613	4756ff76-d053-442f-810f-361073e5a6e1	e4223904-e0cc-48f4-9a35-6a688cd27feb	2026-06-22 21:03:30.549809	f	2026-06-15 21:03:30.550124
6a2a0b8b-3542-4085-9f8b-8974253eb3ae	97375343-31d6-4817-8ac0-7542d847fc49	fcba79bf-1273-4f2f-bc99-f62bf3e4c339	2026-06-22 21:04:25.652724	f	2026-06-15 21:04:25.653024
947119eb-e576-44e4-9a3e-1954e0dc00af	4756ff76-d053-442f-810f-361073e5a6e1	2e4c03ee-09c9-4aac-9cb0-d5f19072afa5	2026-06-23 10:40:56.363982	f	2026-06-16 10:40:56.465695
898a0b77-d460-41e7-abe7-cf3dea45ab7f	97375343-31d6-4817-8ac0-7542d847fc49	2e971038-3ba2-4bce-b0c9-c920bb037abf	2026-06-23 10:41:56.966164	f	2026-06-16 10:41:56.966628
829ca5fc-6b28-4355-bc6a-3ba18325c378	4756ff76-d053-442f-810f-361073e5a6e1	dd5c8def-f4b1-484b-942e-68a6a40d3bbd	2026-06-23 10:43:08.167274	f	2026-06-16 10:43:08.167762
8221d338-80cf-4f87-8c1c-78a9e3c567c0	d40c82fe-c3f5-4105-be8a-b95b7308077f	8c2afee5-4143-4243-88c0-b87c11aafe74	2026-06-23 10:50:28.967782	f	2026-06-16 10:50:28.968167
bf027505-c456-4830-b983-12b6f8e94317	3b8dafc7-ab88-49a0-8699-686d991df1ff	7d26552e-49e4-47b2-8351-de1b7549ca75	2026-06-23 10:52:25.377336	f	2026-06-16 10:52:25.377666
83b22352-f464-4338-bed9-4c772203d1be	3b8dafc7-ab88-49a0-8699-686d991df1ff	d67c99fb-6130-4c68-ab76-d25f8fd4e5b2	2026-06-23 10:53:44.069017	f	2026-06-16 10:53:44.069292
b1ff4e11-3f90-48cd-8d05-627872acb75e	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	a2091aaf-b460-4478-8a00-4a7bf9ccf175	2026-06-23 13:35:20.941953	f	2026-06-16 13:35:21.036544
f13b33a4-409e-4daa-8013-a27de8e44af6	3b8dafc7-ab88-49a0-8699-686d991df1ff	6aaeb40a-45c3-4a8d-80f9-169c036a74b6	2026-06-25 08:49:55.42466	f	2026-06-18 08:49:55.425718
281c84fe-8c3d-4966-8319-678b1165b367	760a3d01-20fa-4f17-8889-36305ef433aa	16173ec8-d5d9-4e3a-9cb5-cc37baa17374	2026-06-25 10:17:07.327342	f	2026-06-18 10:17:07.429849
33ffd141-b800-4371-b750-0faf4f4a32fd	760a3d01-20fa-4f17-8889-36305ef433aa	483eac3b-9b9e-4ab8-a47a-2a7e07a581c9	2026-06-25 10:17:07.326368	f	2026-06-18 10:17:07.429847
50df7202-1094-4d57-8cdc-7df6af2b31cd	760a3d01-20fa-4f17-8889-36305ef433aa	973ff60e-336e-48ae-bee8-9ced22256a67	2026-06-25 10:17:07.328411	f	2026-06-18 10:17:07.429847
7c093f26-d41d-42dc-a478-d301ae8090ea	760a3d01-20fa-4f17-8889-36305ef433aa	2593fbc0-ad26-4907-a9b7-4fbcfa5cb836	2026-06-25 10:17:07.327132	f	2026-06-18 10:17:07.430312
bdc4eef7-da69-4051-b169-4214d130739c	760a3d01-20fa-4f17-8889-36305ef433aa	be413278-10dd-4cc8-ae3a-36f2a9f5ce9c	2026-06-23 18:28:42.316732	t	2026-06-16 18:28:42.319172
fed37813-3d2e-4dfc-81dc-24054958ad10	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	e98af410-6561-474d-ba2c-72af958e60be	2026-06-25 10:50:12.548073	f	2026-06-18 10:50:12.548851
86c2b229-5008-4baf-8d08-bc6866946cae	d40c82fe-c3f5-4105-be8a-b95b7308077f	71adb72a-bcea-40e2-a209-e81e1412db6b	2026-06-25 10:53:47.842413	f	2026-06-18 10:53:47.8428
a78dc07f-4aee-4c33-ab7e-e8bc3e558a0d	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	7ab96563-bfd7-4987-9308-0d2045f488bd	2026-06-25 11:03:22.151704	f	2026-06-18 11:03:22.152163
fc353c15-0d8c-4003-8d6d-6a2d875fc706	d40c82fe-c3f5-4105-be8a-b95b7308077f	f15bb9bf-8e8b-47a2-b9bf-2470375f7b30	2026-06-25 11:06:03.247087	f	2026-06-18 11:06:03.247372
a3e3ebda-dd5b-4a19-9179-072cc6634b3a	3b8dafc7-ab88-49a0-8699-686d991df1ff	72a9e6a1-1636-44ac-8a0a-4fa36afae60c	2026-06-25 19:56:11.004663	f	2026-06-18 19:56:11.104947
20f21840-8be5-423e-bd0d-5dec58f581ba	97375343-31d6-4817-8ac0-7542d847fc49	99ee0032-47c3-446d-a377-6ca34c162c5f	2026-06-25 19:58:29.513136	f	2026-06-18 19:58:29.513457
6d5e9df6-163d-46bb-928f-c2d03ca61277	97375343-31d6-4817-8ac0-7542d847fc49	2b10536f-0790-4160-9862-38dc35ad9478	2026-06-25 10:19:07.13812	t	2026-06-18 10:19:07.138456
6e65eead-630a-4201-9e19-5de07f5ff2bf	3b8dafc7-ab88-49a0-8699-686d991df1ff	5b9c1208-1dfe-4617-a1d5-a7cea49823d0	2026-06-25 19:58:59.216136	f	2026-06-18 19:58:59.216582
4f371f8c-7a67-4fa3-8ecf-63809a3064a1	4756ff76-d053-442f-810f-361073e5a6e1	82e29539-c949-42d2-87e9-2afa5aa5d43d	2026-06-25 20:03:16.601526	f	2026-06-18 20:03:16.601946
fde5af65-0949-4441-addb-c007ff37401e	97375343-31d6-4817-8ac0-7542d847fc49	98a9e37f-1340-46bb-aaf6-bb545147d92b	2026-06-25 20:09:38.711938	f	2026-06-18 20:09:38.712233
b66f2637-eab2-436e-b7c1-8cc75e34ab27	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	31cbebc1-87bb-4e2a-8d84-4db49d9a928f	2026-06-25 20:25:58.507961	f	2026-06-18 20:25:58.508284
eac964f8-8bd2-4372-9636-aaa03c8fc416	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	bce7be93-473d-4cd4-bb5d-460b427543d6	2026-06-25 20:47:58.108002	f	2026-06-18 20:47:58.108598
5a1e082e-3575-456a-906a-5764b66ad11e	ba8e2562-bcff-4027-8210-84caec343ab6	e51d23ad-8776-4b7e-a7eb-332b8d030ea0	2026-06-25 20:49:04.20634	f	2026-06-18 20:49:04.206604
842f6c19-1864-4074-a8c0-a184c6929e8c	4756ff76-d053-442f-810f-361073e5a6e1	d236898a-c1c4-4137-8f16-f43e1d5faf93	2026-06-25 20:57:12.304636	f	2026-06-18 20:57:12.304886
b2f214d8-e0d1-4023-b357-80c83d9ec6aa	d40c82fe-c3f5-4105-be8a-b95b7308077f	0667bfad-b4e5-42cd-baa3-a95df211829f	2026-06-25 21:05:28.406799	f	2026-06-18 21:05:28.407086
4228259b-6867-43a9-89ed-55bc9a8afb6d	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	107075f8-7663-401c-bd0b-c40bccdf6152	2026-06-25 21:09:06.114892	f	2026-06-18 21:09:06.115169
127c2397-4c4b-4995-b0dd-5f1aa1641649	3b8dafc7-ab88-49a0-8699-686d991df1ff	c7e8a23c-d70d-4424-aee3-b00171a71f0e	2026-06-25 21:26:43.816956	f	2026-06-18 21:26:43.817092
6aa1ae96-bb67-426e-8626-6f80c60788c2	d40c82fe-c3f5-4105-be8a-b95b7308077f	54cb8bb3-04eb-4b7e-b9a8-40b490f22074	2026-06-25 23:05:11.108245	f	2026-06-18 23:05:11.108933
8bf7ba33-ae4c-4da1-8aaf-2c2f670da7b8	3b8dafc7-ab88-49a0-8699-686d991df1ff	1b4c8bce-0204-4e10-af87-0afc2469df67	2026-06-25 23:10:42.70943	f	2026-06-18 23:10:42.709739
4eb1f240-68e7-4473-bf9e-62b1ba896081	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	ea60ee11-80d3-4c25-ac6c-4af234ba66c1	2026-06-25 23:12:16.715714	f	2026-06-18 23:12:16.716192
a91ae1ad-b240-41e3-9cd5-11ecad5d0a26	97375343-31d6-4817-8ac0-7542d847fc49	605cbfdc-0b1e-42b9-b233-de6b5478605c	2026-06-25 23:14:33.312685	f	2026-06-18 23:14:33.313278
2f473846-a65c-45e4-a115-86a7e4385442	4756ff76-d053-442f-810f-361073e5a6e1	9e9a0adf-9fb0-47c8-a725-4eae9be718c9	2026-06-25 23:15:06.51783	f	2026-06-18 23:15:06.518079
faaf585e-f3c0-438c-859e-17161950db45	97375343-31d6-4817-8ac0-7542d847fc49	09782335-be47-411e-ace4-156c71c57247	2026-06-25 23:22:26.724797	f	2026-06-18 23:22:26.725042
ae4b19af-35da-4513-9862-80671b0bb409	d40c82fe-c3f5-4105-be8a-b95b7308077f	95223a74-7d03-4905-b765-58c1bb3e643b	2026-06-25 23:24:49.007469	f	2026-06-18 23:24:49.007852
230625c0-02ef-4372-83ad-fde90420bc96	3b8dafc7-ab88-49a0-8699-686d991df1ff	997a51b6-d39b-4cd4-a673-d545f338e570	2026-06-26 04:48:02.224791	f	2026-06-19 04:48:02.321191
9d836a96-1684-4c41-98f3-c45ddc29d582	d40c82fe-c3f5-4105-be8a-b95b7308077f	ec181fe1-e167-4473-9424-09c55e898bb4	2026-06-26 04:51:40.33706	f	2026-06-19 04:51:40.337947
b1028f4e-8103-4708-a315-278e94b0151d	4756ff76-d053-442f-810f-361073e5a6e1	8e9eac5f-9dd2-49c9-a07f-7be091eff9dc	2026-06-26 04:59:16.929762	f	2026-06-19 04:59:16.930021
0b25c406-bc4c-45ec-84de-4c590f7eff73	760a3d01-20fa-4f17-8889-36305ef433aa	8d94fc63-45f3-406c-b5f1-9ac2b4c74e72	2026-06-26 05:33:07.331503	f	2026-06-19 05:33:07.331682
712303f4-417f-4c78-a91c-e967f35bb90a	d40c82fe-c3f5-4105-be8a-b95b7308077f	1460019f-1058-4b84-97db-b5c5f9f3f257	2026-06-26 05:40:05.951985	f	2026-06-19 05:40:05.952968
4610f7c7-e5a3-4a21-8507-6b9669927db4	97375343-31d6-4817-8ac0-7542d847fc49	5f08930a-928a-4b34-96f2-0a2ed21e1455	2026-06-26 07:17:14.333886	f	2026-06-19 07:17:14.635117
069828bb-633e-4128-aea6-64ac5728f4c8	4756ff76-d053-442f-810f-361073e5a6e1	0a6a1c9b-d2cb-40c3-ac0b-8ad676627d0f	2026-06-26 07:18:53.03591	f	2026-06-19 07:18:53.036473
f51761dc-eb21-41f1-8089-387251b4046a	97375343-31d6-4817-8ac0-7542d847fc49	8aaf14d5-9935-41cb-8775-e2b2d1d6e80e	2026-06-26 07:27:08.943172	f	2026-06-19 07:27:08.943524
0d868c80-c48a-43f4-8d2b-bc367e035bef	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	37a56920-292d-48a9-a032-b211c20f443c	2026-06-26 08:32:36.962394	f	2026-06-19 08:32:36.963494
d116c01b-6381-41d4-b105-0c88771b2a7b	1e90003a-b70d-4ed8-9e98-5a86dd3ff011	3d62bb3d-6995-43cf-9267-7b928c83301a	2026-06-26 10:01:05.851985	f	2026-06-19 10:01:05.95021
918b8fa9-5d37-4893-82b9-1171566d81c5	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	f4c3c18f-261a-423b-a2f9-ef7c28148ccd	2026-06-26 10:28:39.66335	f	2026-06-19 10:28:39.663677
873668a8-2c3b-4253-9082-d1bbc92b10c9	3b8dafc7-ab88-49a0-8699-686d991df1ff	ff623bf8-8a97-4345-b558-be8d1113e95e	2026-06-26 10:36:27.6702	f	2026-06-19 10:36:27.670497
2d063017-f126-4b15-a8d5-05d17c3c732a	3b8dafc7-ab88-49a0-8699-686d991df1ff	3ca802d1-0d4f-45c3-86bf-f44da6b7a250	2026-06-26 23:52:18.128958	f	2026-06-19 23:52:18.130061
\.


--
-- Data for Name: supervisors; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.supervisors (id, user_id, facility_id, district, created_at) FROM stdin;
37401a23-44a4-4a69-b036-bbc121de572a	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	c40666ca-46c3-45d7-a3b1-e25faa8f0126	Gasabo	2026-05-30 17:43:06.265013
2ab44cd8-76c7-4fcf-8dbd-48bde35b6013	ba8e2562-bcff-4027-8210-84caec343ab6	c40666ca-46c3-45d7-a3b1-e25faa8f0126	Gasabo	2026-06-18 20:21:20.405593
\.


--
-- Data for Name: system_settings; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.system_settings (id, missed_dose_threshold, low_stock_days, confirm_window_minutes, high_risk_threshold, critical_risk_threshold, updated_at) FROM stdin;
5f417794-653c-43df-ba77-2ebe5c5c29e8	2	14	45	70	85	2026-06-18 18:46:27.392537
\.


--
-- Data for Name: system_users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.system_users (id, full_name, email, phone_number, password_hash, role, is_active, preferred_language, created_at, updated_at, must_change_password, fcm_token, failed_login_attempts, account_locked) FROM stdin;
74e6bcd2-d381-4bc4-988f-99e7948929e1	Valentine Niyonsenga	pt-42b6c368@hivtb.rw	+250783850296	$2a$10$APDX3Lduhg4OgZXZN8L5lOHSRrg1kWs9Kiu5BJtyC9duiW72vnNDq	PATIENT	t	rw	2026-06-06 17:22:19.494692	2026-06-18 20:02:36.708742	t	\N	0	f
17a37f87-3a66-4aec-b809-59fa9f0ae1f0	Celestin Kayumba	celestin@hivtb.rw	0782514692	$2a$10$Y8MKbYlPtKubHxBotXMeyeByF1Kp/PrL9pd68.lYfx4yDtpsHV6ca	SUPERVISOR	t	rw	2026-05-30 17:43:06.263466	2026-06-11 22:33:40.267045	f	\N	0	f
760a3d01-20fa-4f17-8889-36305ef433aa	Marie Uwimana	pt-bd5c2489@hivtb.rw	+250788111222	$2a$10$6s2LGneysGol/QkgeNSVEOqZz.yHsWzuqfeyzcRs4SWlEWSdU743W	PATIENT	t	rw	2026-06-10 17:16:16.018397	2026-06-19 05:33:08.528872	f	dlskZbY6RXq3sPj4LPuSoX:APA91bEIsVpgFYgYRRRIBl6PNnzSyOndB8uJizx-xABJnSqQ5uFfGN7Wws-Sz9lquQxVN18EjZf_PGGddxDrqrFYS3nuupSZ_toDY25C4nWe9eEPkWUqUXA	0	f
3414701f-3738-4f15-8e0e-e84cd8692388	Innocent Nishimwe	pt-f56814de@hivtb.rw	+250788312512	$2a$10$Ux4k3J9eaBMwEyEQ8Hm7YOSSkhRXa6laGk44W531yZC3wREGL3FRK	PATIENT	t	rw	2026-05-30 16:49:12.509035	2026-06-02 17:01:11.103471	f	\N	0	f
786e213d-d2bd-4541-883f-73e3b51e7da0	Dr. Marie Uwimana	dr.marie@dmc.rw	+250788000001	$2a$10$2lhuBAiGqFt4LVyEBFn88OCtrwgmDJH/DZhJ18ah3NGF731RZLq1W	FACILITY_PROVIDER	t	rw	2026-06-05 11:36:52.684041	2026-06-05 11:36:52.684067	t	\N	0	f
bfc6d975-2caf-4940-abb7-fb0598fb9d17	Test SMS	pt-ed686138@hivtb.rw	+25079129125	$2a$10$CmpCgOP0pyxxXiB2ZUZrRuxrV8Y7ItlrIRvcehXaihhPhyXI1Rnhy	PATIENT	t	rw	2026-06-09 08:14:11.459296	2026-06-09 08:14:11.459317	t	\N	0	f
b23bc14f-1785-4679-bdbe-81f0c1b779f2	Jean Damascene	pt-eef83cce@hivtb.rw	+250798422578	$2a$10$SRENmvxgFlbHG6x66VPlCuZhgkxQzksithlxBiCE4h6IpjUkZ0OlS	PATIENT	t	rw	2026-06-10 10:24:20.337261	2026-06-10 14:26:03.980725	t	\N	0	f
8eafea8b-ea87-4458-abc6-d2a3192b3461	Immaculee Nyiraneza	pt-b40e538e@hivtb.rw	+250788555666	$2a$10$MCroTWS/.WOMY7FQoE7SWedg44SAef3j6hM0dIRuOwB40qhLAk6w.	PATIENT	t	rw	2026-06-10 17:16:27.330824	2026-06-10 17:16:27.330847	t	\N	0	f
d1d6dd86-79c5-4b85-a20b-7f17483ccfde	Eric Mugisha	pt-f952b170@hivtb.rw	+250788777999	$2a$10$fVtTBrqqji9nzQSgWsjJTOCq9ZKUtShikeWcXWaVt/utp0vBqrova	PATIENT	t	rw	2026-06-11 18:57:46.56298	2026-06-11 18:57:46.563003	t	\N	0	f
d40c82fe-c3f5-4105-be8a-b95b7308077f	chadia Rwema	chadia@hivtb.rw	0788121352	$2a$10$nPUPF38RdWFHrC.LDQDO8ONkxiQgRf.g19bCn6IlQ1xGre3rO1I9m	FACILITY_PROVIDER	t	rw	2026-05-30 17:41:55.969993	2026-06-19 05:40:05.560407	f	\N	0	f
e93c9287-a7a8-4769-8076-086e7a36545d	Pierre Hakizimana	pt-5a587173@hivtb.rw	+250788000005	$2a$10$PO9KWwsNKnwLYNuTTASvT.x/hiS5q78PnAYBaIesO2Oyu4IKChueS	PATIENT	t	rw	2026-06-19 05:41:33.75351	2026-06-19 05:41:33.753532	t	\N	\N	\N
2d267ac3-e651-4eb3-b12d-282bbf8496a9	SMOKETEST Jean Mukiza	pt-24e38433@hivtb.rw	0788000111	$2a$10$QmaqlDoHxcvJpyxIRSS9B.TKnZfR5SnXTOZqXxwUd8SQSjuHo/LaS	PATIENT	t	rw	2026-06-14 21:41:03.307981	2026-06-14 21:41:03.308002	t	\N	\N	\N
3870dcd3-2a10-41e7-893e-c3dc2c6c88df	Jeanne Mukamana	pt-d719b939@hivtb.rw	0788123456	$2a$10$N2GXhBDIvRhcgYuDtMliE.kehTG.jBpUoFAvoAqxxxlJgkpJF21MO	PATIENT	t	rw	2026-06-15 08:43:54.942027	2026-06-15 08:43:54.942052	t	\N	\N	\N
1e90003a-b70d-4ed8-9e98-5a86dd3ff011	John Nkusi	john@dmc.rw	0788654470	$2a$10$3EDrLuJjBXR5es86LLnEueAygJIuG7ImSFYXGD8H9i4ZmTtFjShUC	FACILITY_PROVIDER	t	rw	2026-06-18 20:15:20.114075	2026-06-18 20:47:58.104666	f	\N	0	\N
ba8e2562-bcff-4027-8210-84caec343ab6	Jean Pierre Nkurunziza	j.pierre@dmc.rw	0788654410	$2a$10$FuSu2tis03SRXNTa8idcQOIYdZ0XDE/vSMOw6gsdIAq3FHT9aw7M.	SUPERVISOR	t	rw	2026-06-18 20:21:20.404871	2026-06-18 20:49:52.50568	f	\N	0	\N
97375343-31d6-4817-8ac0-7542d847fc49	Jean Pierre	jean@hivtb.rw	0788245123	$2a$10$o.6Cg9kiRSdA3IKJTIW21.g0uUQ4ftK5Q9TOjzMVE3D3Y92Yxhc1G	CHW	t	rw	2026-05-30 16:51:17.519241	2026-06-19 07:27:08.932968	f	dlskZbY6RXq3sPj4LPuSoX:APA91bEIsVpgFYgYRRRIBl6PNnzSyOndB8uJizx-xABJnSqQ5uFfGN7Wws-Sz9lquQxVN18EjZf_PGGddxDrqrFYS3nuupSZ_toDY25C4nWe9eEPkWUqUXA	0	f
3b8dafc7-ab88-49a0-8699-686d991df1ff	System Administrator	admin@hivtb.rw	+250780000000	$2a$10$A267Zap52.0UoCbvAKgpoO8TzrbYn6K1XdKYZapEULC8emtIy68H2	SYSTEM_ADMIN	t	en	2026-05-30 15:53:04.594792	2026-06-18 21:26:43.814385	f	dtk5u2ZISIaZBsOCL6c7Ye:APA91bEJU1sEFkCKanAB7t7zGoTZq44GZhKcmE7P_AWNFfd5z9REeiYOD3M7SdAkgVa0eJQ5vP-L7k47Tu6vahYiO9IpJ9Kzzv3xPUalDMmqv-RDQGX0l4E	0	f
4756ff76-d053-442f-810f-361073e5a6e1	Alice Uwimana	chw1@hivtb.rw	+250781000001	$2a$10$83zFdSIXh90xA3dJTAipxupOLXgThubhKLbBERHvqiiwJV12F06Mm	CHW	t	rw	2026-05-30 15:53:06.333896	2026-06-19 04:59:19.721111	f	dlskZbY6RXq3sPj4LPuSoX:APA91bEIsVpgFYgYRRRIBl6PNnzSyOndB8uJizx-xABJnSqQ5uFfGN7Wws-Sz9lquQxVN18EjZf_PGGddxDrqrFYS3nuupSZ_toDY25C4nWe9eEPkWUqUXA	0	f
\.


--
-- Data for Name: tracing_tasks; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tracing_tasks (id, patient_id, chw_id, missed_appointment_date, days_since_missed, reason, status, ltfu_confirmed_at, outcome, disengagement_reason, resolution_plan, proxy_authorized, proxy_name, notes, escalated_to, created_at, resolved_at) FROM stdin;
8e30eb4b-0488-4f86-921a-592e9aa287dd	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-06	5	MISSED_APPOINTMENT	RESOLVED	2026-06-11 09:53:01.776286	PATIENT_FOUND	TRANSPORT_COST	CHW will arrange transport voucher and re-link to facility next visit	f	\N	Patient found, agreed to return to clinic	\N	2026-06-11 09:52:25.675277	2026-06-11 09:53:34.873142
80d28047-e5c5-411d-a415-53d664f05001	bfb73641-679c-4161-8587-b48754d8ea9b	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-04	7	MISSED_APPOINTMENT	RESOLVED	\N	PATIENT_FOUND	WORK_RELOCATION	patient agreed to restart treatment	f	\N	anxiety has big part	\N	2026-06-11 20:20:51.488213	2026-06-18 21:19:09.003412
de009294-0916-4de1-ab20-46290fb7e097	96fbf0ac-9af5-439b-9730-1d8198fcddea	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-01	18	MISSED_APPOINTMENT	CHW_ASSIGNED	\N	\N	\N	\N	f	\N	\N	\N	2026-06-19 06:00:00.069642	\N
cbc0273b-7972-4686-a04d-3065b16a6080	49ca4358-c32e-44ba-bc52-92d731ea8f5b	1dd0bb68-cd37-4945-846c-8309613c438a	2026-05-07	43	LOST_TO_FOLLOWUP	ESCALATED	2026-06-11 09:54:20.87521	\N	\N	\N	f	\N	\N	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	2026-06-11 09:54:19.776634	\N
65aaf941-1d05-4983-a824-741fc6ff01f9	9034f0b1-8d54-4d8a-83c3-f723077062b2	1dd0bb68-cd37-4945-846c-8309613c438a	2026-06-10	9	MISSED_REFILL	LATE	\N	\N	\N	\N	f	\N	\N	\N	2026-06-15 20:03:00.5358	\N
966687ed-aa4e-4aa7-acd6-7d88b6b9bf98	6b2a9c4a-9845-406f-bf96-5e92606e3309	1dd0bb68-cd37-4945-846c-8309613c438a	2026-05-20	30	LOST_TO_FOLLOWUP	ESCALATED	2026-06-11 20:21:06.797322	\N	\N	\N	f	\N	Home visit conducted. Patient confirmed not residing at registered address for over 3 weeks. Neighbors report patient relocated.	17a37f87-3a66-4aec-b809-59fa9f0ae1f0	2026-06-11 20:20:52.389422	\N
36eab4a7-2490-43f0-ac4d-21add5bfc4ce	b0cbee7c-c691-4c7d-873c-2404b3b7b3ff	02a4d824-e859-45e5-9092-40dc69fa4c89	2026-06-09	10	MISSED_REFILL	RESOLVED	\N	PATIENT_FOUND	TRANSPORT_COST	Patient Agreed to restart treatment with transport convin	f	\N	further observed	\N	2026-06-11 20:20:50.086483	2026-06-19 07:21:33.533126
e571c328-4d13-42d4-abd7-1bdbe1f06417	dce2bd61-cee2-4aac-a559-d537bbd6d175	1dd0bb68-cd37-4945-846c-8309613c438a	2026-05-26	24	MISSED_APPOINTMENT	RESOLVED	\N	PATIENT_FOUND	OTHER	patient agreed to start treatment again next Monday	f	\N	\N	\N	2026-06-15 20:15:57.613629	2026-06-19 07:28:23.482205
\.


--
-- Data for Name: treatment_plans; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.treatment_plans (id, patient_id, start_date, end_date, is_active, fhir_care_plan_id, created_at, updated_at, medication_name, dosage, frequency, sync_status, created_by) FROM stdin;
7381bc34-97bb-4390-8e1d-131bd6f201d7	6b2a9c4a-9845-406f-bf96-5e92606e3309	2026-04-11	\N	t	1041	2026-06-10 17:16:29.629125	2026-06-10 19:25:06.343088	Rifafour (RHZE)	3 tablets	Once daily	SYNCED	d40c82fe-c3f5-4105-be8a-b95b7308077f
a2b3f1d6-65ca-4d9a-bbcd-7a3a9e1ce95e	9034f0b1-8d54-4d8a-83c3-f723077062b2	2026-06-10	2026-12-10	t	\N	2026-06-10 10:51:38.318226	2026-06-10 19:25:06.342941	TLD (Tenofovir/Lamivudine/Dolutegravir) + RHZE	1 tablet TLD + 1 tablet RHZE	Once daily	PENDING	d40c82fe-c3f5-4105-be8a-b95b7308077f
736698df-631a-4c49-84d9-1483d1773751	49ca4358-c32e-44ba-bc52-92d731ea8f5b	2025-12-12	\N	t	\N	2026-06-10 17:16:19.921189	2026-06-10 19:25:06.343027	Tenofovir/Lamivudine/Dolutegravir (TLD)	1 tablet	Once daily	PENDING	d40c82fe-c3f5-4105-be8a-b95b7308077f
0df3febf-4104-4319-ab8d-cdb29f9cb384	dce2bd61-cee2-4aac-a559-d537bbd6d175	2026-06-01	\N	t	\N	2026-06-11 18:57:48.357025	2026-06-11 20:05:37.091334	Tenofovir/Lamivudine/Dolutegravir (TLD)	1 tablet	Once daily	PENDING	d40c82fe-c3f5-4105-be8a-b95b7308077f
eab48139-8c15-411c-95ee-0f30d2ddc7a0	8009c775-9249-45bf-8f23-5270418f75d1	2026-06-15	\N	t	\N	2026-06-15 08:55:05.543294	2026-06-15 08:55:05.543313	Tenofovir/Lamivudine/Dolutegravir (TLD)	1 tablet	Once daily	PENDING	d40c82fe-c3f5-4105-be8a-b95b7308077f
d0566c6a-1595-4222-8bc1-f4f037627c0f	46bb83b4-0890-4763-b54e-1ec649d5062b	2026-06-18	\N	t	\N	2026-06-18 11:09:25.338064	2026-06-18 11:09:25.338081	EFV	1TABLET	ONCE_DAILY	PENDING	d40c82fe-c3f5-4105-be8a-b95b7308077f
61d23afc-09dc-49f2-9524-114207471d12	bfb73641-679c-4161-8587-b48754d8ea9b	2026-06-19	\N	t	\N	2026-06-18 23:03:49.209303	2026-06-18 23:03:49.209326	TDF	1table(300mg)	ONCE_DAILY	PENDING	1e90003a-b70d-4ed8-9e98-5a86dd3ff011
\.


--
-- Name: locations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.locations_id_seq', 1, false);


--
-- Name: medication_records_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.medication_records_id_seq', 117, true);


--
-- Name: ai_risk_scores ai_risk_scores_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ai_risk_scores
    ADD CONSTRAINT ai_risk_scores_pkey PRIMARY KEY (id);


--
-- Name: alerts alerts_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.alerts
    ADD CONSTRAINT alerts_pkey PRIMARY KEY (id);


--
-- Name: audit_logs audit_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_pkey PRIMARY KEY (id);


--
-- Name: chws chws_employee_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chws
    ADD CONSTRAINT chws_employee_code_key UNIQUE (employee_code);


--
-- Name: chws chws_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chws
    ADD CONSTRAINT chws_pkey PRIMARY KEY (id);


--
-- Name: confirmation_logs confirmation_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmation_logs
    ADD CONSTRAINT confirmation_logs_pkey PRIMARY KEY (id);


--
-- Name: dose_schedules dose_schedules_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dose_schedules
    ADD CONSTRAINT dose_schedules_pkey PRIMARY KEY (id);


--
-- Name: facilities facilities_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facilities
    ADD CONSTRAINT facilities_pkey PRIMARY KEY (id);


--
-- Name: facility_providers facility_providers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facility_providers
    ADD CONSTRAINT facility_providers_pkey PRIMARY KEY (id);


--
-- Name: fhir_sync_logs fhir_sync_logs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fhir_sync_logs
    ADD CONSTRAINT fhir_sync_logs_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- Name: home_visits home_visits_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.home_visits
    ADD CONSTRAINT home_visits_pkey PRIMARY KEY (id);


--
-- Name: lab_results lab_results_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lab_results
    ADD CONSTRAINT lab_results_pkey PRIMARY KEY (id);


--
-- Name: locations locations_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_code_key UNIQUE (code);


--
-- Name: locations locations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (id);


--
-- Name: medication_records medication_records_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.medication_records
    ADD CONSTRAINT medication_records_pkey PRIMARY KEY (id);


--
-- Name: patients patients_fhir_patient_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_fhir_patient_id_key UNIQUE (fhir_patient_id);


--
-- Name: patients patients_national_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_national_id_key UNIQUE (national_id);


--
-- Name: patients patients_patient_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_patient_code_key UNIQUE (patient_code);


--
-- Name: patients patients_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_pkey PRIMARY KEY (id);


--
-- Name: patients patients_referral_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_referral_id_key UNIQUE (referral_id);


--
-- Name: referrals referrals_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.referrals
    ADD CONSTRAINT referrals_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- Name: refresh_tokens refresh_tokens_token_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_token_key UNIQUE (token);


--
-- Name: supervisors supervisors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisors
    ADD CONSTRAINT supervisors_pkey PRIMARY KEY (id);


--
-- Name: system_settings system_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_settings
    ADD CONSTRAINT system_settings_pkey PRIMARY KEY (id);


--
-- Name: system_users system_users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_users
    ADD CONSTRAINT system_users_email_key UNIQUE (email);


--
-- Name: system_users system_users_phone_number_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_users
    ADD CONSTRAINT system_users_phone_number_key UNIQUE (phone_number);


--
-- Name: system_users system_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.system_users
    ADD CONSTRAINT system_users_pkey PRIMARY KEY (id);


--
-- Name: tracing_tasks tracing_tasks_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tracing_tasks
    ADD CONSTRAINT tracing_tasks_pkey PRIMARY KEY (id);


--
-- Name: treatment_plans treatment_plans_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.treatment_plans
    ADD CONSTRAINT treatment_plans_pkey PRIMARY KEY (id);


--
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- Name: idx_ai_risk_scores_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_ai_risk_scores_patient_id ON public.ai_risk_scores USING btree (patient_id);


--
-- Name: idx_alerts_chw_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_alerts_chw_id ON public.alerts USING btree (chw_id);


--
-- Name: idx_alerts_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_alerts_patient_id ON public.alerts USING btree (patient_id);


--
-- Name: idx_audit_logs_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_audit_logs_user_id ON public.audit_logs USING btree (user_id);


--
-- Name: idx_confirmation_logs_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_confirmation_logs_patient_id ON public.confirmation_logs USING btree (patient_id);


--
-- Name: idx_dose_schedules_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_dose_schedules_patient_id ON public.dose_schedules USING btree (patient_id);


--
-- Name: idx_dose_schedules_plan_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_dose_schedules_plan_id ON public.dose_schedules USING btree (plan_id);


--
-- Name: idx_home_visits_chw_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_home_visits_chw_id ON public.home_visits USING btree (chw_id);


--
-- Name: idx_home_visits_client_request_id; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_home_visits_client_request_id ON public.home_visits USING btree (client_request_id) WHERE (client_request_id IS NOT NULL);


--
-- Name: idx_home_visits_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_home_visits_patient_id ON public.home_visits USING btree (patient_id);


--
-- Name: idx_lab_results_patient_loinc_date; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_lab_results_patient_loinc_date ON public.lab_results USING btree (patient_id, loinc_code, observed_at DESC);


--
-- Name: idx_lab_results_patient_loinc_observed_at; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_lab_results_patient_loinc_observed_at ON public.lab_results USING btree (patient_id, loinc_code, observed_at);


--
-- Name: idx_locations_location_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_locations_location_type ON public.locations USING btree (location_type);


--
-- Name: idx_locations_parent_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_locations_parent_id ON public.locations USING btree (parent_id);


--
-- Name: idx_medication_records_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_medication_records_patient_id ON public.medication_records USING btree (patient_id);


--
-- Name: idx_patients_chw_assignment_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_patients_chw_assignment_status ON public.patients USING btree (chw_assignment_status) WHERE ((chw_assignment_status)::text = 'PENDING'::text);


--
-- Name: idx_patients_chw_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_patients_chw_id ON public.patients USING btree (chw_id);


--
-- Name: idx_patients_facility_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_patients_facility_id ON public.patients USING btree (facility_id);


--
-- Name: idx_patients_referral_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_patients_referral_id ON public.patients USING btree (referral_id);


--
-- Name: idx_patients_registration_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_patients_registration_status ON public.patients USING btree (registration_status);


--
-- Name: idx_patients_user_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_patients_user_id ON public.patients USING btree (user_id);


--
-- Name: idx_referrals_chw_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_referrals_chw_id ON public.referrals USING btree (referred_by_chw_id);


--
-- Name: idx_referrals_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_referrals_patient_id ON public.referrals USING btree (patient_id);


--
-- Name: idx_referrals_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_referrals_status ON public.referrals USING btree (status);


--
-- Name: idx_system_users_fcm_token; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_system_users_fcm_token ON public.system_users USING btree (fcm_token) WHERE (fcm_token IS NOT NULL);


--
-- Name: idx_tracing_tasks_chw_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tracing_tasks_chw_id ON public.tracing_tasks USING btree (chw_id);


--
-- Name: idx_tracing_tasks_created_at; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tracing_tasks_created_at ON public.tracing_tasks USING btree (created_at);


--
-- Name: idx_tracing_tasks_patient_date_open; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX idx_tracing_tasks_patient_date_open ON public.tracing_tasks USING btree (patient_id, missed_appointment_date) WHERE ((status)::text <> 'RESOLVED'::text);


--
-- Name: idx_tracing_tasks_patient_id; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tracing_tasks_patient_id ON public.tracing_tasks USING btree (patient_id);


--
-- Name: idx_tracing_tasks_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_tracing_tasks_status ON public.tracing_tasks USING btree (status);


--
-- Name: ai_risk_scores ai_risk_scores_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.ai_risk_scores
    ADD CONSTRAINT ai_risk_scores_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: alerts alerts_chw_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.alerts
    ADD CONSTRAINT alerts_chw_id_fkey FOREIGN KEY (chw_id) REFERENCES public.chws(id);


--
-- Name: alerts alerts_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.alerts
    ADD CONSTRAINT alerts_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: alerts alerts_provider_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.alerts
    ADD CONSTRAINT alerts_provider_id_fkey FOREIGN KEY (provider_id) REFERENCES public.facility_providers(id);


--
-- Name: alerts alerts_resolved_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.alerts
    ADD CONSTRAINT alerts_resolved_by_fkey FOREIGN KEY (resolved_by) REFERENCES public.system_users(id);


--
-- Name: alerts alerts_supervisor_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.alerts
    ADD CONSTRAINT alerts_supervisor_id_fkey FOREIGN KEY (supervisor_id) REFERENCES public.supervisors(id);


--
-- Name: audit_logs audit_logs_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.audit_logs
    ADD CONSTRAINT audit_logs_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.system_users(id);


--
-- Name: chws chws_facility_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chws
    ADD CONSTRAINT chws_facility_id_fkey FOREIGN KEY (facility_id) REFERENCES public.facilities(id);


--
-- Name: chws chws_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.chws
    ADD CONSTRAINT chws_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.system_users(id);


--
-- Name: confirmation_logs confirmation_logs_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmation_logs
    ADD CONSTRAINT confirmation_logs_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: confirmation_logs confirmation_logs_plan_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmation_logs
    ADD CONSTRAINT confirmation_logs_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES public.treatment_plans(id);


--
-- Name: confirmation_logs confirmation_logs_schedule_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.confirmation_logs
    ADD CONSTRAINT confirmation_logs_schedule_id_fkey FOREIGN KEY (schedule_id) REFERENCES public.dose_schedules(id);


--
-- Name: dose_schedules dose_schedules_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dose_schedules
    ADD CONSTRAINT dose_schedules_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.system_users(id);


--
-- Name: dose_schedules dose_schedules_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dose_schedules
    ADD CONSTRAINT dose_schedules_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: dose_schedules dose_schedules_plan_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.dose_schedules
    ADD CONSTRAINT dose_schedules_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES public.treatment_plans(id);


--
-- Name: facility_providers facility_providers_facility_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facility_providers
    ADD CONSTRAINT facility_providers_facility_id_fkey FOREIGN KEY (facility_id) REFERENCES public.facilities(id);


--
-- Name: facility_providers facility_providers_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.facility_providers
    ADD CONSTRAINT facility_providers_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.system_users(id);


--
-- Name: fhir_sync_logs fhir_sync_logs_chw_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fhir_sync_logs
    ADD CONSTRAINT fhir_sync_logs_chw_id_fkey FOREIGN KEY (chw_id) REFERENCES public.chws(id);


--
-- Name: home_visits home_visits_chw_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.home_visits
    ADD CONSTRAINT home_visits_chw_id_fkey FOREIGN KEY (chw_id) REFERENCES public.chws(id);


--
-- Name: home_visits home_visits_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.home_visits
    ADD CONSTRAINT home_visits_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: lab_results lab_results_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.lab_results
    ADD CONSTRAINT lab_results_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: locations locations_parent_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_parent_id_fkey FOREIGN KEY (parent_id) REFERENCES public.locations(id);


--
-- Name: medication_records medication_records_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.medication_records
    ADD CONSTRAINT medication_records_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: medication_records medication_records_plan_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.medication_records
    ADD CONSTRAINT medication_records_plan_id_fkey FOREIGN KEY (plan_id) REFERENCES public.treatment_plans(id);


--
-- Name: patients patients_chw_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_chw_id_fkey FOREIGN KEY (chw_id) REFERENCES public.chws(id);


--
-- Name: patients patients_confirmed_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_confirmed_by_fkey FOREIGN KEY (confirmed_by) REFERENCES public.system_users(id);


--
-- Name: patients patients_facility_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_facility_id_fkey FOREIGN KEY (facility_id) REFERENCES public.facilities(id);


--
-- Name: patients patients_screened_by_chw_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_screened_by_chw_id_fkey FOREIGN KEY (screened_by_chw_id) REFERENCES public.chws(id);


--
-- Name: patients patients_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.patients
    ADD CONSTRAINT patients_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.system_users(id);


--
-- Name: referrals referrals_confirmed_by_provider_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.referrals
    ADD CONSTRAINT referrals_confirmed_by_provider_id_fkey FOREIGN KEY (confirmed_by_provider_id) REFERENCES public.facility_providers(id);


--
-- Name: referrals referrals_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.referrals
    ADD CONSTRAINT referrals_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: referrals referrals_referred_by_chw_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.referrals
    ADD CONSTRAINT referrals_referred_by_chw_id_fkey FOREIGN KEY (referred_by_chw_id) REFERENCES public.chws(id);


--
-- Name: refresh_tokens refresh_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.system_users(id);


--
-- Name: supervisors supervisors_facility_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisors
    ADD CONSTRAINT supervisors_facility_id_fkey FOREIGN KEY (facility_id) REFERENCES public.facilities(id);


--
-- Name: supervisors supervisors_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisors
    ADD CONSTRAINT supervisors_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.system_users(id);


--
-- Name: tracing_tasks tracing_tasks_chw_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tracing_tasks
    ADD CONSTRAINT tracing_tasks_chw_id_fkey FOREIGN KEY (chw_id) REFERENCES public.chws(id);


--
-- Name: tracing_tasks tracing_tasks_escalated_to_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tracing_tasks
    ADD CONSTRAINT tracing_tasks_escalated_to_fkey FOREIGN KEY (escalated_to) REFERENCES public.system_users(id);


--
-- Name: tracing_tasks tracing_tasks_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tracing_tasks
    ADD CONSTRAINT tracing_tasks_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- Name: treatment_plans treatment_plans_created_by_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.treatment_plans
    ADD CONSTRAINT treatment_plans_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.system_users(id);


--
-- Name: treatment_plans treatment_plans_patient_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.treatment_plans
    ADD CONSTRAINT treatment_plans_patient_id_fkey FOREIGN KEY (patient_id) REFERENCES public.patients(id);


--
-- PostgreSQL database dump complete
--

\unrestrict aXsSEDsjbPuPcREd9Zm7lWoYKA4TZoSlQ2XejX1gHvIq8vYw51ryrdxa25KWR16

