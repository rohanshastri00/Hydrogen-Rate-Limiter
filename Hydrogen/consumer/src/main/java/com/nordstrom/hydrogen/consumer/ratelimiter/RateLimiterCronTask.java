package com.nordstrom.hydrogen.consumer.ratelimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.RateLimiter;

import com.nordstrom.hydrogen.consumer.utility.RateLimiterHelper;
import com.nordstrom.sharedlib.logging.ApplicationLogger;

@Component
public class RateLimiterCronTask {

  @Autowired
  private ApplicationLogger logger;

  @Autowired
  private RateLimiter rateLimiter;

  @Autowired
  private RateLimiterHelper rateLimiterHelper;

  @Scheduled(cron = "0 */1 * * * *")
  public void updateIngestionRate() throws Exception {

    Double oldIngestionRate = rateLimiter.getRate();
    Double newIngestionRate = rateLimiterHelper.getNewIngestionRate();

    if (!oldIngestionRate.equals(newIngestionRate)) {
      rateLimiter.setRate(newIngestionRate);
      logger.info("Adjust ingestion rate from %f permits per second to %f permits per second",
              oldIngestionRate, newIngestionRate);
    } else {
      logger.debug("Ingestion rate remains at %f permits per second", newIngestionRate);
    }
  }
}
