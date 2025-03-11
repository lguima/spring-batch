package com.batch.job.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledJobLauncher {
  @Autowired
  JobLauncher jobLauncher;

  @Autowired
  Job job;

  @Scheduled(cron = "${job.scheduling.cron}")
  public void launchJob() throws Exception {
    jobLauncher.run(job, new JobParameters());
  }
}
