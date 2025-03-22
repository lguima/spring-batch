package com.batch.job.config;

import com.batch.job.job.FileDeletingTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class JobConfig {

  @Bean
  public Job taskletJob(JobRepository jobRepository, Step step) {
    return new JobBuilder("taskletJob", jobRepository)
      .start(step)
      .build();
  }

  @Bean
  public Step deleteFilesInDir(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("deleteFilesInDir", jobRepository)
      .tasklet(new FileDeletingTasklet(), transactionManager)
      .build();
  }
}
