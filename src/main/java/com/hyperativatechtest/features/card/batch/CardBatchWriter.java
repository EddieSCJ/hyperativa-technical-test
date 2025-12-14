package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.entity.Card;
import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import com.hyperativatechtest.features.common.crypto.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardBatchWriter implements ItemWriter<ParsedCard> {

    private final EncryptionService encryptionService;
    private final CardBatchInsertService cardBatchInsertService;

    private String lotId;
    private String username;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        lotId = stepExecution.getJobParameters().getString("lotId");
        username = stepExecution.getJobParameters().getString("username");
    }

    @Override
    public void write(Chunk<? extends ParsedCard> chunk) {
        List<Card> cardsToInsert = new ArrayList<>();
        int errors = 0;

        for (ParsedCard parsedCard : chunk) {
            try {
                String cardHash = encryptionService.hash(parsedCard.getCardNumber());
                String encryptedCardNumber = encryptionService.encrypt(parsedCard.getCardNumber());

                Card card = Card.builder()
                        .encryptedCardNumber(encryptedCardNumber)
                        .cardHash(cardHash)
                        .lotId(lotId)
                        .sequenceNumber(parsedCard.getSequenceNumber())
                        .createdBy(username)
                        .build();

                cardsToInsert.add(card);
            } catch (Exception e) {
                log.error("Error processing card at line {}: {}", parsedCard.getLineNumber(), e.getMessage());
                errors++;
            }
        }

        if (!cardsToInsert.isEmpty()) {
            int inserted = cardBatchInsertService.batchInsertIgnoreDuplicates(cardsToInsert);
            log.info("Chunk: inserted {} cards, errors: {}", inserted, errors);
        }
    }
}
