package com.batch.job.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduledJobLauncher {
  @Autowired
  JobLauncher jobLauncher;

  @Autowired
  Job job;

  @Autowired
  private ExpirableLockRegistry lockRegistry;

  @Value("${job.lock.registryKey}")
  private String registryKey;

  @Value("${job.lock.maxWaitTime}")
  private Integer maxWaitTime;

  @Scheduled(cron = "${job.scheduling.cron}")
  public void launchJob() throws Exception {
    Lock lock = lockRegistry.obtain(registryKey);
    log.debug("Lock: " + lock.toString());

    boolean lockAcquired =  lock.tryLock(maxWaitTime, TimeUnit.SECONDS);
    log.debug("Lock Acquired: " + lockAcquired);

    if (!lockAcquired) {
      throw new Exception("Lock not acquired");
    }

    try {
      jobLauncher.run(job, new JobParameters());
    } finally {
      lock.unlock();
    }
  }
}
