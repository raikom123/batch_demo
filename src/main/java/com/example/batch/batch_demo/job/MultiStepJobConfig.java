package com.example.batch.batch_demo.job;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import com.example.batch.batch_demo.config.JobExecutionLoggingListener;
import com.example.batch.batch_demo.config.StepExecutionLoggingListener;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MultiStepJobConfig {

    @Bean
    Job multiStepJob(JobRepository jobRepository, Step taskletStep, Step fileImportStep) {
        return new JobBuilder("multiStepJob", jobRepository)
                .start(taskletStep)
                .next(fileImportStep)
                .build();
    }

    // @Bean
    // Step firstStep(JobRepository jobRepository,
    // PlatformTransactionManager transactionManager,
    // Tasklet firstStepTasklet) {
    // return new StepBuilder("firstStep", jobRepository)
    // .tasklet(firstStepTasklet, transactionManager)
    // .build();
    // }

    // @StepScope
    // @Bean
    // Tasklet firstStepTasklet(@Value("#{jobParameters['param']}") String param) {
    // return (stepContribution, chunkContext) -> {
    // log.info("firstStep: {}", chunkContext.getStepContext().getStepName());
    // log.info("param: {}", param);
    // return RepeatStatus.FINISHED;
    // };
    // }

    // @Bean
    // Step secondStep(JobRepository jobRepository,
    // PlatformTransactionManager transactionManager,
    // ItemReader<String> itemReader,
    // ItemWriter<String> itemWriter) {
    // return new StepBuilder("secondStep", jobRepository)
    // .<String, String>chunk(10, transactionManager)
    // .reader(itemReader)
    // .writer(itemWriter)
    // .build();
    // }

    // @Bean
    // ItemReader<String> itemReader() {
    // return new ListItemReader<>(List.of("aaa", "bbb", "ccc", "ddd", "eee"));
    // }

    // @Bean
    // ItemWriter<String> itemWriter() {
    // return (items) -> log.info("execute write: {}", items);
    // }

}
