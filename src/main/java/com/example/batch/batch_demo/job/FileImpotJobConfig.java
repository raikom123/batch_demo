package com.example.batch.batch_demo.job;

import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.batch_demo.config.JobExecutionLoggingListener;
import com.example.batch.batch_demo.config.StepExecutionLoggingListener;

import lombok.extern.slf4j.Slf4j;

// @Configuration
@Slf4j
public class FileImpotJobConfig {

    @Bean
    Job fileImportJob(JobRepository jobRepository, Step fileImportStep) {
        return new JobBuilder("fileImportJob", jobRepository)
                .start(fileImportStep)
                .listener(new JobExecutionLoggingListener())
                // .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step fileImportStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("fileImportStep", jobRepository)
                .<String, String>chunk(2, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .listener(new StepExecutionLoggingListener())
                // .allowStartIfComplete(true)
                .listener(new ItemWriteListener<String>() {
                    @Override
                    public void beforeWrite(@NonNull Chunk<? extends String> items) {
                        // log.info("beforeWrite: {}", items);
                    }

                    @Override
                    public void afterWrite(@NonNull Chunk<? extends String> item) {
                        item.getItems().forEach(log::info);
                    }

                    @Override
                    public void onWriteError(@NonNull Exception ex, @NonNull Chunk<? extends String> item) {
                        log.error("onWriteError: {}", item, ex);
                    }
                })
                .build();
    }

    ItemReader<String> itemReader() {
        return new FlatFileItemReaderBuilder<String>()
                .name("fileImportItemReader")
                .resource(new ClassPathResource("data/batch_test.csv"))
                .lineMapper((line, lineNumber) -> line)
                .linesToSkip(1)
                .build();
    }

    ItemProcessor<String, String> itemProcessor() {
        return item -> {
            if (item.contains("error")) {
                throw new RuntimeException("error found");
            }
            return item.toUpperCase();
        };
    }

    ItemWriter<String> itemWriter() {
        return new FlatFileItemWriterBuilder<String>()
                .name("fileImportItemWriter")
                .resource(new FileSystemResource("data/batch_test_output.csv"))
                .lineAggregator(item -> item)
                .build();
    }

}
