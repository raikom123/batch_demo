package com.example.batch.batch_demo.job;

import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import com.example.batch.batch_demo.config.JobExecutionLoggingListener;
import com.example.batch.batch_demo.config.StepExecutionLoggingListener;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class TaskletJobConfig {

    @Bean
    Job taskletJob(JobRepository jobRepository, Step taskletStep) {
        return new JobBuilder("taskletJob", jobRepository)
                .start(taskletStep)
                .build();
    }

    @Bean
    Step taskletStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager) {
        return new StepBuilder("taskletStep", jobRepository)
                .tasklet(tasklet(), transactionManager)
                .build();
    }

    Tasklet tasklet() {
        return new TaskletImpl();
    }

    private class TaskletImpl implements Tasklet, StepExecutionListener {

        private StepExecution stepExecution;

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            var context = stepExecution.getJobExecution().getExecutionContext();
            var count = context.getInt("count", 0);
            log.info("count: {}", count++);
            context.putInt("count", count);
            if (count < 5) {
                return RepeatStatus.CONTINUABLE;
            }
            return RepeatStatus.FINISHED;
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
            this.stepExecution = stepExecution;
        }
    }

}
