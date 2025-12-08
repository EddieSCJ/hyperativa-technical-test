INSERT INTO role_permissions (role_id, permission) VALUES
((SELECT id FROM roles WHERE name = 'ADMIN'), 'user:create'),
((SELECT id FROM roles WHERE name = 'ADMIN'), 'user:read'),
((SELECT id FROM roles WHERE name = 'ADMIN'), 'user:update'),
((SELECT id FROM roles WHERE name = 'ADMIN'), 'user:delete'),
((SELECT id FROM roles WHERE name = 'ADMIN'), 'card:create'),
((SELECT id FROM roles WHERE name = 'ADMIN'), 'card:read'),
((SELECT id FROM roles WHERE name = 'ADMIN'), 'card:update'),
((SELECT id FROM roles WHERE name = 'ADMIN'), 'card:delete'),
((SELECT id FROM roles WHERE name = 'USER'), 'card:create'),
((SELECT id FROM roles WHERE name = 'USER'), 'card:read');

