CREATE TABLE IF NOT EXISTS cards (
    id VARCHAR(36) PRIMARY KEY,
    encrypted_card_number TEXT NOT NULL,
    card_hash VARCHAR(64) NOT NULL UNIQUE,
    lot_id VARCHAR(50),
    sequence_number INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    CONSTRAINT fk_cards_created_by FOREIGN KEY (created_by) REFERENCES users(username) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_cards_card_hash ON cards(card_hash);
CREATE INDEX IF NOT EXISTS idx_cards_lot_id ON cards(lot_id);
CREATE INDEX IF NOT EXISTS idx_cards_created_at ON cards(created_at);
CREATE INDEX IF NOT EXISTS idx_cards_created_by ON cards(created_by);

