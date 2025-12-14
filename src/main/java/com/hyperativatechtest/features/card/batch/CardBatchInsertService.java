package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.entity.Card;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardBatchInsertService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public int batchInsertIgnoreDuplicates(List<Card> cards) {
        if (cards.isEmpty()) {
            return 0;
        }

        String sql = buildBatchInsertSql(cards);
        List<Object> params = buildParams(cards);

        int inserted = jdbcTemplate.update(sql, params.toArray());
        log.debug("Batch inserted {} cards (duplicates ignored)", inserted);
        return inserted;
    }

    private String buildBatchInsertSql(List<Card> cards) {
        StringBuilder sql = new StringBuilder(
                "INSERT INTO cards (encrypted_card_number, card_hash, lot_id, sequence_number, created_by, created_at) VALUES "
        );

        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(?, ?, ?, ?, ?, NOW())");
        }

        sql.append(" ON CONFLICT (card_hash) DO NOTHING");
        return sql.toString();
    }

    private List<Object> buildParams(List<Card> cards) {
        List<Object> params = new ArrayList<>();

        for (Card card : cards) {
            params.add(card.getEncryptedCardNumber());
            params.add(card.getCardHash());
            params.add(card.getLotId());
            params.add(card.getSequenceNumber());
            params.add(card.getCreatedBy());
        }

        return params;
    }
}
