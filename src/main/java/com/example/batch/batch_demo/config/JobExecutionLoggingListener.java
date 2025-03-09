package com.example.batch.batch_demo.config;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.boot.ExitCodeGenerator;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JobExecutionLoggingListener implements JobExecutionListener, ExitCodeGenerator {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Job started");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job finished");
    }

    @Override
    public int getExitCode() {
        // TODO Auto-generated method stub
        return 0;
    }

}
