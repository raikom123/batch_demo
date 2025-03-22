package com.example.batch.batch_demo.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.lang.NonNull;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobExecutionLoggingListener implements JobExecutionListener, ExitCodeGenerator {

    @Override
    public void beforeJob(@NonNull JobExecution jobExecution) {
        log.info("Job started: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public void afterJob(@NonNull JobExecution jobExecution) {
        log.info("Job finished: {}", jobExecution.getJobInstance().getJobName());
    }

    @Override
    public int getExitCode() {
        return 0;
    }

}
