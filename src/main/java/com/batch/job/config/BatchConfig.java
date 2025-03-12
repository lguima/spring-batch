package com.batch.job.config;

import com.batch.job.job.FileDeletingTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {
  @Bean
  public Job taskletJob(JobRepository jobRepository, Step step) {
    return new JobBuilder("taskletJob", jobRepository)
      .start(step)
      .build();
  }

  @Bean
  public Step deleteFilesInDir(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("deleteFilesInDir", jobRepository)
      .tasklet(fileDeletingTasklet(), transactionManager)
      .build();
  }

  @Bean
  public FileDeletingTasklet fileDeletingTasklet() {
    FileDeletingTasklet tasklet = new FileDeletingTasklet();

    tasklet.setDirectoryResource(new FileSystemResource("target/test-outputs/test-dir"));

    return tasklet;
  }
}
