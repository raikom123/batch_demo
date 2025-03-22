package com.example.batch.batch_demo.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.lang.NonNull;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StepExecutionLoggingListener implements StepExecutionListener {

    @Override
    public void beforeStep(@NonNull StepExecution stepExecution) {
        log.info("Step started: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(@NonNull StepExecution stepExecution) {
        log.info("Step finished: {}", stepExecution.getStepName());
        return stepExecution.getExitStatus();
    }

}
