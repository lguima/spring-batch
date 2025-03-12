package com.batch.job.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.Assert;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Configuration
@EnableRetry
@Slf4j
public class FileDeletingTasklet implements Tasklet, InitializingBean {
  @Autowired
  private ExpirableLockRegistry lockRegistry;

  private Resource directory;

  @Retryable(maxAttemptsExpression = "${job.retry.maxAttempts}", backoff = @Backoff(delayExpression = "${job.retry.delay}", maxDelayExpression = "${job.retry.maxDelay}"))
  public RepeatStatus execute(StepContribution contribution,
                              ChunkContext chunkContext) throws Exception {
    log.debug("Step Contribution: " + contribution);
    log.debug("Chunk Context: " + chunkContext);

    Lock lock = lockRegistry.obtain("lock00");
    log.debug("Lock: " + lock.toString());

    boolean lockAcquired =  lock.tryLock(1, TimeUnit.SECONDS);
    log.debug("Lock Acquired: " + lockAcquired);

    if (!lockAcquired) {
      throw new Exception("Lock not acquired");
    }

    try {
      System.out.println("Deleting files from directory: " + directory.getFilename());

      log.debug("Delaying");
      Thread.sleep(10000);

      File dir = directory.getFile();
      Assert.state(dir.isDirectory(), "The resource must be a directory");

      File[] files = dir.listFiles();

      for (int i = 0; i < files.length; i++) {
        boolean deleted = files[i].delete();
        if (!deleted) {
          throw new UnexpectedJobExecutionException("Could not delete file " +
            files[i].getPath());
        }
      }

      return RepeatStatus.FINISHED;
    } finally {
      lock.unlock();
    }
  }

  public void setDirectoryResource(Resource directory) {
    this.directory = directory;
  }

  public void afterPropertiesSet() throws Exception {
    Assert.state(directory != null, "Directory must be set");
  }
}
