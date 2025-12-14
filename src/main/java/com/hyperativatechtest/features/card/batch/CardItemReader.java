package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class CardItemReader implements ItemReader<ParsedCard> {

    private Iterator<ParsedCard> iterator;

    public void setCards(List<ParsedCard> cards) {
        this.iterator = new ArrayList<>(cards).iterator();
    }

    @Override
    public ParsedCard read() {
        if (iterator != null && iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}

