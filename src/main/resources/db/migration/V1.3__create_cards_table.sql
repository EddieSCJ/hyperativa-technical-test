CREATE TABLE IF NOT EXISTS cards (
    id VARCHAR(36) PRIMARY KEY,
    encrypted_card_number VARCHAR(255) NOT NULL,
    card_hash VARCHAR(64) NOT NULL UNIQUE,
    lot_id VARCHAR(50),
    sequence_number INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    CONSTRAINT fk_cards_created_by FOREIGN KEY (created_by) REFERENCES users(username) ON DELETE SET NULL
);

