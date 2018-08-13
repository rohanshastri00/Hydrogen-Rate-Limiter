package com.nordstrom.hydrogen.consumer.dependency;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nordstrom.hydrogen.consumer.config.ApplicationConfig;
import com.nordstrom.sharedlib.config.KafkaConsumerOptions;
import com.nordstrom.sharedlib.config.LoggingOptions;
import com.nordstrom.sharedlib.http.HttpClient;
import com.nordstrom.sharedlib.http.NordHttpClient;
import com.nordstrom.sharedlib.kafka.KafkaConsumerClient;
import com.nordstrom.sharedlib.kafka.KafkaConsumerFactory;
import com.nordstrom.sharedlib.logging.ApplicationLogger;
import com.nordstrom.sharedlib.logging.JsonLogger;
import com.nordstrom.sharedlib.s3.NordS3Client;
import com.nordstrom.sharedlib.s3.S3ClientFactory;
import com.nordstrom.sharedlib.sqs.SdpSqsClient;
import com.nordstrom.sharedlib.sqs.SqsClientFactory;
import com.nordstrom.sharedlib.stream.ConsumerClient;
import com.nordstrom.sharedlib.utility.JsonHelper;
import com.nordstrom.sharedlib.utility.YamlHelper;

@Configuration
public class SharedLibraryBeanBootstrapper {

  @Bean
  public ApplicationLogger configureLogger(ApplicationConfig applicationConfig) {
    LoggingOptions options = prepareLoggingOptions(applicationConfig);
    return new JsonLogger(options);
  }

  @Bean
  public NordS3Client nordS3Client(ApplicationLogger logger) {
    S3ClientFactory clientFactory = new S3ClientFactory();
    return new NordS3Client(clientFactory, logger);
  }
  
  @Bean
  public JsonHelper jsonHelper() {
    return new JsonHelper();
  }

  @Bean
  public HttpClient httpClient() {
    return new NordHttpClient();
  }

  @Bean
  public SdpSqsClient sdpSqsClient(ApplicationLogger logger) {
    SqsClientFactory clientFactory = new SqsClientFactory();
    return new SdpSqsClient(clientFactory, logger);
  }

  @Bean
  public List<ConsumerClient> consumerClient(ApplicationConfig applicationConfig, ApplicationLogger logger) {
    KafkaConsumerOptions consumerOptions = prepareKafkaConsumerOptions(applicationConfig);
    KafkaConsumerFactory consumerFactory = new KafkaConsumerFactory(consumerOptions);
    return IntStream.range(0, applicationConfig.getNumberOfConsumersPerNode()).mapToObj(index -> {
      return new KafkaConsumerClient(logger, consumerFactory, consumerOptions);
    }).collect(Collectors.toList());
  }

  @Bean
  public YamlHelper yamlHelper() {
    return new YamlHelper();
  }

  private LoggingOptions prepareLoggingOptions(ApplicationConfig applicationConfig) {
    LoggingOptions options = new LoggingOptions()
            .withEnvironment(applicationConfig.getEnvironment())
            .withLoggingApplicationId(applicationConfig.getLoggingApplicationId())
            .withLoggingNameSpace(applicationConfig.getLoggingNamespace());
    return options;
  }

  private KafkaConsumerOptions prepareKafkaConsumerOptions(ApplicationConfig applicationConfig) {
    KafkaConsumerOptions consumerOptions = new KafkaConsumerOptions()
            .withEnvironment(applicationConfig.getEnvironment())
            .withBootstrapServer(applicationConfig.getKafkaBootstrapServers())
            .withGroupId(applicationConfig.getKafkaGroupId())
            .withTopic(applicationConfig.getKafkaSkuTopic())
            .withMaxPollRecords(applicationConfig.getMaxPollRecords())
            .withRequestTimeoutMs(60000)
            .withSessionTimeoutMs(55000)
            .withHeartbeatIntervalMs(50000);
    return consumerOptions;
  }
}
