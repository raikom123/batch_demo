package com.example.batch.batch_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.batch.batch_demo.config.JobExecutionLoggingListener;

@SpringBootApplication
public class BatchDemoApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(BatchDemoApplication.class, args),
				new JobExecutionLoggingListener()));
	}

}
