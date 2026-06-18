ALTER TABLE home_visits ADD COLUMN client_request_id UUID;
CREATE UNIQUE INDEX idx_home_visits_client_request_id ON home_visits (client_request_id) WHERE client_request_id IS NOT NULL;
