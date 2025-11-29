-- landlord A UUID (e.g., 34e1ae65-4363-4ecd-8641-ce1600edc2e9)
-- landlord B UUID (e.g., f4371680-0ddd-4d64-86aa-62a4b8de7067)
INSERT INTO maintenance_requests (id, landlord_id, property_id, title, description, status, created_at, updated_at)
VALUES
-- Vlad
  (gen_random_uuid(), '34e1ae65-4363-4ecd-8641-ce1600edc2e9', 'prop-101',
   'HVAC noise', 'Blower makes loud grinding noise', 'IN_PROGRESS', NOW(), NOW()),
-- Landlord4 Four
  (gen_random_uuid(), 'f4371680-0ddd-4d64-86aa-62a4b8de7067', 'prop-202',
   'Broken window', 'Cracked living room pane', 'OPEN', NOW(), NOW());

-- Demo agent verification state
INSERT INTO agents (id, status, created_at, updated_at)
VALUES
  ('agent-123', 'PENDING', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Demo tenancy in pending state
INSERT INTO tenancies (id, landlord_id, property_id, tenant_id, tenant_status, created_at, updated_at)
VALUES
  (gen_random_uuid(), '34e1ae65-4363-4ecd-8641-ce1600edc2e9', 'prop-101', 'T-1001', 'PENDING', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Demo properties
INSERT INTO properties (id, landlord_id, name, address, postcode, rent, status, paid, created_at)
VALUES
  ('prop-101', '34e1ae65-4363-4ecd-8641-ce1600edc2e9', '22 Anthony House', 'Pembury Place', 'E5 8GZ', 2000, 'Occupied', TRUE, NOW()),
  ('PROP-2002', 'landlord-demo', 'Central Gate', 'Commercial Road', 'E1 1LN', 1750, 'Vacant', FALSE, NOW()),
  ('PROP-2003', 'landlord-demo', 'High Street Flat', 'High Street', 'E2 6AB', 1450, 'Occupied', TRUE, NOW())
ON CONFLICT DO NOTHING;

-- Demo documents
INSERT INTO documents (id, tenancy_id, name, type, url, shared_with, created_at)
SELECT gen_random_uuid(), t.id, 'Sample tenancy agreement', 'AGREEMENT', '/documents/sample.pdf', 'ALL', NOW()
FROM tenancies t
LIMIT 1;

-- Demo users for invite lookup
INSERT INTO users (id, email, role, created_at)
VALUES
  (gen_random_uuid(), 'danise@example.com', 'tenant', NOW()),
  (gen_random_uuid(), 'owner@example.com', 'owner', NOW()),
  (gen_random_uuid(), 'agent@example.com', 'agent', NOW()),
  (gen_random_uuid(), 'test@example11.com', 'tenant', NOW())
ON CONFLICT DO NOTHING;
