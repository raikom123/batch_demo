package com.example.batch.batch_demo.job;

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
import org.springframework.batch.item.adapter.ItemWriterAdapter;
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

@Configuration
@Slf4j
public class FileImpotJobConfig {

    @Bean
    Job fileImportJob(JobRepository jobRepository, Step fileImportStep) {
        return new JobBuilder("fileImportJob", jobRepository)
                .start(fileImportStep)
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
                // allowStartIfCompleteにtrueを設定することで完了したジョブを再実行できる
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
        return String::toUpperCase; // 入力データ（文字列）を大文字に変換
    }

    ItemWriter<String> itemWriter() {
        return new ItemWriteListenerImpl();
    }

    private class ItemWriteListenerImpl implements ItemWriter<String>, StepExecutionListener {

        private StepExecution stepExecution;

        @Override
        public void write(@NonNull Chunk<? extends String> chunk) throws Exception {
            log.info("write: {}", chunk);
            log.info("count: {}", stepExecution.getJobExecution().getExecutionContext().getInt("count"));
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
            this.stepExecution = stepExecution;
        }

    }

}
