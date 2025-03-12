package com.batch.job.config;

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
  private static final String LOCK_REGISTRY_REDIS_KEY = "lock00";
  private static final Duration RELEASE_TIME_DURATION = Duration.ofSeconds(15);

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
    configuration.setHostName("localhost");
    configuration.setPort(6379);
    configuration.setDatabase(0);

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
      LOCK_REGISTRY_REDIS_KEY,
      RELEASE_TIME_DURATION.toMillis()
    );
  }
}
