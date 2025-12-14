-- Insert default admin user for local development ONLY
-- Username: admin
-- Password: Admin123! (BCrypt hash)

INSERT INTO users (username, password, role_id, enabled, account_non_locked, credentials_non_expired, account_non_expired)
SELECT 'admin', '$2a$12$51PHPLFxBR6IxK5HZa2h9.1bFDXfZErdSnvTMQPd99lFmAyauW3km', id, true, true, true, true
FROM roles WHERE name = 'ADMIN'
ON CONFLICT (username) DO NOTHING;