package com.nordstrom.hydrogen.consumer.dependency;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.util.concurrent.RateLimiter;
import com.nordstrom.hydrogen.consumer.config.RatelimiterConfig;
import com.nordstrom.hydrogen.consumer.model.SolrStatus;
import com.nordstrom.hydrogen.consumer.utility.RateCalculator;

@Configuration
public class RateLimiterBeanBootstrapper {
  @Bean
  public RateLimiter rateLimiter(RatelimiterConfig config) {
    return RateLimiter.create(config.getRateMapping().get(SolrStatus.DEFAULT.name()));
  }

  @Bean
  public RateCalculator rateCalculator() {
    return new RateCalculator();
  }
}
