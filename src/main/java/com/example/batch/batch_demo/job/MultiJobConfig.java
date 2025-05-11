package com.example.batch.batch_demo.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MultiJobConfig {

        @Bean
        Job job1(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager) {
                return new JobBuilder("job1", jobRepository)
                                .start(new StepBuilder("job1_step", jobRepository)
                                                .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED,
                                                                transactionManager)
                                                .build())
                                .build();
        }

        @Bean
        Job job2(JobRepository jobRepository,
                        PlatformTransactionManager transactionManager) {
                return new JobBuilder("job2", jobRepository)
                                .start(new StepBuilder("job2_step", jobRepository)
                                                .tasklet((stepContribution, chunkContext) -> RepeatStatus.FINISHED,
                                                                transactionManager)
                                                .build())
                                .build();
        }

}
