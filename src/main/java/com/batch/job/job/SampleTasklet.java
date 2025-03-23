package com.batch.job.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

import java.util.random.RandomGenerator;

@Configuration
@EnableRetry
@Slf4j
public class FileDeletingTasklet implements Tasklet, InitializingBean {

  @Retryable(maxAttemptsExpression = "${job.retry.maxAttempts}", backoff = @Backoff(delayExpression = "${job.retry.delay}", maxDelayExpression = "${job.retry.maxDelay}"))
  public RepeatStatus execute(StepContribution contribution,
                              ChunkContext chunkContext) throws Exception {
    log.debug("Executing Tasklet...");

    int randomNum = RandomGenerator.getDefault().nextInt(0, 10);
    log.debug("Random: " + randomNum);

    if ((randomNum & 1) != 0) {
      log.debug("Odd number");
      throw new IllegalStateException("Failing job...");
    }

    int delay = 5000;
    log.debug("Delaying " + delay + "ms");
    Thread.sleep(delay);

    log.debug("Tasklet executed!");
    return RepeatStatus.FINISHED;
  }

  public void afterPropertiesSet() throws Exception {}
}
