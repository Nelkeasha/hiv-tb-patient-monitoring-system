-- V28: Tamper-evident hash chain for audit_logs.
-- Each new entry's hash covers its own fields plus the previous entry's
-- hash, so altering or deleting a past row breaks every hash after it.
-- Columns are nullable because the chain only has meaning for entries
-- computed at write time going forward — backfilling a hash for rows
-- written before this column existed would be fake tamper-evidence (anyone
-- could compute the same value after the fact), so existing rows are left
-- as the un-chained "genesis" period and the real chain starts with the
-- first row inserted after this migration.
ALTER TABLE audit_logs
    ADD COLUMN entry_hash    VARCHAR(64),
    ADD COLUMN previous_hash VARCHAR(64);

CREATE INDEX idx_audit_logs_entry_hash ON audit_logs(entry_hash) WHERE entry_hash IS NOT NULL;
