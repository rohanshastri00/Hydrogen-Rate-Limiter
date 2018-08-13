package com.nordstrom.hydrogen.consumer.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "ratelimiter")
public class RatelimiterConfig {

  private int msSolrRecoverIngestionDelay;

  private Map<String, Double> rateMapping;

  public Map<String, Double> getRateMapping() {
    return rateMapping;
  }

  public void setRateMapping(Map<String, Double> rateMapping) {
    this.rateMapping = rateMapping;
  }

  public int getMsSolrRecoverIngestionDelay() {
    return msSolrRecoverIngestionDelay;
  }

  public void setMsSolrRecoverIngestionDelay(int msSolrRecoverIngestionDelay) {
    this.msSolrRecoverIngestionDelay = msSolrRecoverIngestionDelay;
  }
}
