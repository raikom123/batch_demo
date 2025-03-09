package com.example.batch.batch_demo.job.fileimport;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
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
                .<String, String>chunk(2, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .allowStartIfComplete(true)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        log.info("beforeStep: {}", stepExecution);
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        log.info("afterStep: {}", stepExecution);
                        return stepExecution.getExitStatus();
                    }
                })
                .listener(new ItemReadListener<String>() {
                    @Override
                    public void beforeRead() {
                        log.info("beforeRead");
                    }

                    @Override
                    public void afterRead(String item) {
                        log.info("afterRead: {}", item);
                    }

                    @Override
                    public void onReadError(Exception ex) {
                        log.error("onReadError", ex);
                    }
                })
                .listener(new ItemProcessListener<String, String>() {
                    @Override
                    public void beforeProcess(String item) {
                        log.info("beforeProcess: {}", item);
                    }

                    @Override
                    public void afterProcess(String item, String result) {
                        log.info("afterProcess: {} -> {}", item, result);
                    }

                    @Override
                    public void onProcessError(String item, Exception e) {
                        log.error("onProcessError: {}", item, e);
                    }
                })
                .listener(new ItemWriteListener<String>() {
                    @Override
                    public void beforeWrite(Chunk<? extends String> items) {
                        log.info("beforeWrite: {}", items);
                    }

                    @Override
                    public void afterWrite(Chunk<? extends String> item) {
                        log.info("afterWrite: {}", item);
                    }

                    @Override
                    public void onWriteError(Exception ex, Chunk<? extends String> item) {
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
        return String::toUpperCase;
    }

    ItemWriter<String> itemWriter() {
        return items -> items.forEach(log::info);
    }

}
