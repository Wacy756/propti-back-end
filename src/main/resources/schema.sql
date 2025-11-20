DROP TABLE IF EXISTS sessions CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Maintenance requests table for landlord-scoped tickets
CREATE TABLE IF NOT EXISTS maintenance_requests (
    id UUID PRIMARY KEY,
    landlord_id VARCHAR(255) NOT NULL,
    property_id VARCHAR(255),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_maintenance_landlord_status
    ON maintenance_requests (landlord_id, status);
