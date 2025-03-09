package com.example.batch.batch_demo.job.fileimport;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.batch_demo.config.JobExecutionLoggingListener;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class FileImpotJobConfig {

    @Bean
    Job fileImportJob(JobRepository jobRepository, Step fileImportStep) {
        return new JobBuilder("fileImportJob", jobRepository)
                .start(fileImportStep)
                .listener(new JobExecutionLoggingListener())
                .build();
    }

    @Bean
    Step fileImportStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("fileImportStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .allowStartIfComplete(true)
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
        return String::toUpperCase;
    }

    ItemWriter<String> itemWriter() {
        return items -> items.forEach(log::info);
    }

}
