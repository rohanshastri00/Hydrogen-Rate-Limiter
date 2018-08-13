package com.nordstrom.hydrogen.consumer.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nordstrom.hydrogen.consumer.config.RatelimiterConfig;
import com.nordstrom.hydrogen.consumer.model.RiskLevel;
import com.nordstrom.hydrogen.consumer.model.SolrStatus;
import com.nordstrom.hydrogen.consumer.processor.MetricsProcessor;

@Component
public class RateLimiterConfigHelper {

  @Autowired
  private RatelimiterConfig config;

  @Autowired
  private MetricsProcessor metricsProcessor;

  @Autowired
  private RateCalculator rateCalculator;

  public Double getRateBySolrStatus(SolrStatus solrStatus) {
      return config.getRateMapping().get(solrStatus.name());
  }

  public Double getRateByMetrics() {

      RiskLevel riskLevel = metricsProcessor.getRiskLevel();
      return rateCalculator.calculateRate(riskLevel);

  }

}
