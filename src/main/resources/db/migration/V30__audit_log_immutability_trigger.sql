-- V30: True database-level immutability for audit_logs.
--
-- V28 added a hash chain (entry_hash/previous_hash) which is tamper-EVIDENT —
-- it lets you detect after the fact that a row was altered, but does nothing
-- to stop the alteration happening in the first place. The system's stated
-- compliance claim is that audit log rows cannot be updated or deleted at
-- all, which requires an actual database-enforced constraint, not just a
-- detection mechanism. A BEFORE UPDATE/DELETE trigger is used here rather
-- than REVOKE-based privilege separation because the application connects
-- to Postgres with a single role used for all CRUD across every table —
-- splitting that role out for this one table alone would be a bigger,
-- riskier change than a trigger that simply rejects the two operations.

CREATE OR REPLACE FUNCTION reject_audit_log_mutation()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'audit_logs rows are immutable — % is not permitted (id=%)',
        TG_OP, COALESCE(OLD.id, NULL);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER audit_logs_no_update
    BEFORE UPDATE ON audit_logs
    FOR EACH ROW
    EXECUTE FUNCTION reject_audit_log_mutation();

CREATE TRIGGER audit_logs_no_delete
    BEFORE DELETE ON audit_logs
    FOR EACH ROW
    EXECUTE FUNCTION reject_audit_log_mutation();
