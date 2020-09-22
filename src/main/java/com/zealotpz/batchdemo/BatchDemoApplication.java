package com.zealotpz.batchdemo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zealotpz
 */

//启用批处理
@EnableBatchProcessing
@SpringBootApplication
public class BatchDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchDemoApplication.class, args);
	}

}
