package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CardBatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CardBatchProcessor cardBatchProcessor;
    private final CardBatchWriter cardBatchWriter;

    @Bean
    public Job cardProcessingJob(Step cardProcessingStep) {
        return new JobBuilder("cardProcessingJob", jobRepository)
                .start(cardProcessingStep)
                .build();
    }

    @Bean
    public Step cardProcessingStep(ItemReader<ParsedCard> cardItemReader) {
        return new StepBuilder("cardProcessingStep", jobRepository)
                .<ParsedCard, ParsedCard>chunk(1000, transactionManager)
                .reader(cardItemReader)
                .processor(cardBatchProcessor)
                .writer(cardBatchWriter)
                .build();
    }

    @Bean
    public CardItemReader cardItemReader() {
        return new CardItemReader();
    }
}

