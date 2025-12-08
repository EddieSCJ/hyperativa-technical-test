ALTER TABLE users ADD COLUMN role_id BIGINT AFTER role;

ALTER TABLE users ADD CONSTRAINT fk_users_role_id
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE RESTRICT;

CREATE INDEX idx_users_role_id ON users(role_id);

