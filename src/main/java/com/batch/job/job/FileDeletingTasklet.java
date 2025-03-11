package com.batch.job.job;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.Assert;

import java.io.File;

@Configuration
@EnableRetry
public class FileDeletingTasklet implements Tasklet, InitializingBean {
  private Resource directory;

  @Retryable(maxAttemptsExpression = "${job.retry.maxAttempts}", backoff = @Backoff(delayExpression = "${job.retry.delay}", maxDelayExpression = "${job.retry.maxDelay}"))
  public RepeatStatus execute(StepContribution contribution,
                              ChunkContext chunkContext) throws Exception {
    System.out.println("Deleting files from directory: " + directory.getFilename());

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
  }

  public void setDirectoryResource(Resource directory) {
    this.directory = directory;
  }

  public void afterPropertiesSet() throws Exception {
    Assert.state(directory != null, "Directory must be set");
  }
}
