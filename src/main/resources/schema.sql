DROP TABLE IF EXISTS sessions CASCADE;
DROP TABLE IF EXISTS properties CASCADE;
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

-- Tenancies: tracks tenant activation state
CREATE TABLE IF NOT EXISTS properties (
    id VARCHAR(255) PRIMARY KEY,
    landlord_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    postcode VARCHAR(50),
    rent INTEGER,
    status VARCHAR(50) NOT NULL DEFAULT 'Vacant',
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tenancies (
    id UUID PRIMARY KEY,
    landlord_id VARCHAR(255) NOT NULL,
    property_id VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    tenant_name VARCHAR(255),
    tenant_email VARCHAR(255),
    owner_id VARCHAR(255),
    owner_email VARCHAR(255),
    start_date VARCHAR(50),
    monthly_rent INTEGER,
    tenant_status VARCHAR(50) NOT NULL,
    invite_sent_at TIMESTAMP WITHOUT TIME ZONE,
    invite_accepted_at TIMESTAMP WITHOUT TIME ZONE,
    reference_status VARCHAR(50),
    agreement_status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Documents: stored files linked to a tenancy (e.g., AST, compliance)
CREATE TABLE IF NOT EXISTS documents (
    id UUID PRIMARY KEY,
    tenancy_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    url VARCHAR(500),
    shared_with VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Agents: simple verification state
CREATE TABLE IF NOT EXISTS agents (
    id VARCHAR(255) PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Users: basic directory for invite lookups
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL,
    name VARCHAR(255),
    phone VARCHAR(50),
    company_name VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);

-- Invites: track tenancy invitations
CREATE TABLE IF NOT EXISTS invites (
    id UUID PRIMARY KEY,
    tenant_email VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255),
    tenancy_id UUID,
    inviter_role VARCHAR(50),
    inviter_name VARCHAR(255),
    property_address VARCHAR(255),
    invitee_role VARCHAR(50) DEFAULT 'tenant',
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW()
);
