package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardBatchService {

    private final JobLauncher jobLauncher;
    private final Job cardProcessingJob;
    private final CardItemReader cardItemReader;

    public void processBatch(List<ParsedCard> cards, String lotId, String username) {
        try {
            cardItemReader.setCards(cards);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("lotId", lotId)
                    .addString("username", username)
                    .addLong("timestamp", Instant.now().toEpochMilli())
                    .toJobParameters();

            jobLauncher.run(cardProcessingJob, jobParameters);
            log.info("Batch job completed for {} cards with lotId: {}", cards.size(), lotId);
        } catch (Exception e) {
            log.error("Error running batch job: {}", e.getMessage());
            throw new RuntimeException("Batch processing failed: " + e.getMessage(), e);
        }
    }
}

