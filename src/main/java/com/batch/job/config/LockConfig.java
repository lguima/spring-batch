package com.batch.job.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.ExpirableLockRegistry;

import java.time.Duration;

@Configuration
public class LockConfig {
  @Value("${job.lock.redis.hostname}")
  private String hostname;

  @Value("${job.lock.redis.port}")
  private Integer port;

  @Value("${job.lock.redis.database}")
  private Integer database;

  @Value("${job.lock.redis.registryKey}")
  private String registryKey;

  @Value("${job.lock.redis.releaseTimeDuration}")
  private Integer releaseTimeDuration;

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName(hostname);
    configuration.setPort(port);
    configuration.setDatabase(database);

    return new JedisConnectionFactory(configuration);
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    return template;
  }

  @Bean
  public ExpirableLockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory) {
    return new RedisLockRegistry(
      redisConnectionFactory,
      registryKey,
      Duration.ofSeconds(releaseTimeDuration).toMillis()
    );
  }
}
