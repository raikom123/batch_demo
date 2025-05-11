package com.example.batch.batch_demo.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MultiStepJobConfig {

    @Bean
    Job multiStepJob(JobRepository jobRepository, @Qualifier("taskletStep") Step taskletStep,
            @Qualifier("fileImportStep") Step fileImportStep) {
        return new JobBuilder("multiStepJob", jobRepository)
                .start(taskletStep)
                .next(fileImportStep)
                .build();
    }

}
