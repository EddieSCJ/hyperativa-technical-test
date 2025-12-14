package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardBatchProcessor implements ItemProcessor<ParsedCard, ParsedCard> {

    @Override
    public ParsedCard process(ParsedCard parsedCard) {
        if (parsedCard == null || !isValidCardNumber(parsedCard.getCardNumber())) {
            log.warn("Invalid card at line {}", parsedCard != null ? parsedCard.getLineNumber() : "unknown");
            return null;
        }
        return parsedCard;
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        int digits = cardNumber.replaceAll("\\D", "").length();
        return digits >= 13 && digits <= 19;
    }
}

