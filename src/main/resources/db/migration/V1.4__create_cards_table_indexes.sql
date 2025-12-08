CREATE INDEX IF NOT EXISTS idx_cards_card_hash ON cards(card_hash);
CREATE INDEX IF NOT EXISTS idx_cards_lot_id ON cards(lot_id);
CREATE INDEX IF NOT EXISTS idx_cards_created_at ON cards(created_at);
CREATE INDEX IF NOT EXISTS idx_cards_created_by ON cards(created_by);

