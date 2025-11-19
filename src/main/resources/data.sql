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
