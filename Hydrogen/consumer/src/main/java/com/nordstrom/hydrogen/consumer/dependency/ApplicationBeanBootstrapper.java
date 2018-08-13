package com.nordstrom.hydrogen.consumer.dependency;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import com.google.common.util.concurrent.RateLimiter;

import com.nordstrom.hydrogen.consumer.config.ApplicationConfig;
import com.nordstrom.hydrogen.consumer.config.RatelimiterConfig;
import com.nordstrom.hydrogen.consumer.domain.ConsumerPollGate;
import com.nordstrom.hydrogen.consumer.domain.StreamConsumer;
import com.nordstrom.hydrogen.consumer.processor.StreamConsumerProcessor;
import com.nordstrom.hydrogen.consumer.utility.RateLimiterHelper;
import com.nordstrom.hydrogen.consumer.utility.ResourceFileReader;
import com.nordstrom.hydrogen.consumer.utility.ThreadHelper;
import com.nordstrom.sharedlib.logging.ApplicationLogger;
import com.nordstrom.sharedlib.stream.ConsumerClient;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;

@Configuration
@EnableRetry
public class ApplicationBeanBootstrapper {

  @Autowired
  private ApplicationConfig applicationConfig;

  @Autowired
  private List<ConsumerClient> consumerClients;

  @Autowired
  private ApplicationLogger logger;

  @Autowired
  private StreamConsumerProcessor streamConsumerProcessor;

  @Autowired
  private RateLimiter rateLimiter;

  @Autowired
  private ThreadHelper threadHelper;

  @Autowired
  private RatelimiterConfig ratelimiterConfig;

  @Autowired
  private RateLimiterHelper rateLimiterHelper;

  @Bean
  public ExecutorService executorService() {
    return Executors.newFixedThreadPool(applicationConfig.getNumberOfConsumersPerNode());
  }

  @Bean
  public StatsDClient consumerStatsDClient() {
    return new NonBlockingStatsDClient("hydrogen.consumer", "localhost", 8125, new String[] { applicationConfig.getEnvironment() });
  }

  @Bean
  public List<StreamConsumer> configureStreamConsumerPool(StatsDClient statsDClient) {
    return consumerClients.stream().map(consumerClient -> {
      ConsumerPollGate consumerPollGate = new ConsumerPollGate(rateLimiterHelper, ratelimiterConfig,
              logger, rateLimiter, threadHelper);

      StreamConsumer kafkaConsumer = new StreamConsumer(logger, applicationConfig, consumerClient,
              consumerPollGate, statsDClient, streamConsumerProcessor);
      return kafkaConsumer;
    }).collect(Collectors.toList());
  }

  @Bean
  public ForkJoinPool createConsumerThreadPool() {
    return new ForkJoinPool(applicationConfig.getNumberOfThreadsPerConsumer());
  }

  @Bean
  public ResourceFileReader resourceFileReader() {
    return new ResourceFileReader();
  }

}
