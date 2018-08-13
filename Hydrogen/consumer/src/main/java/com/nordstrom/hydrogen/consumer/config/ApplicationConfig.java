package com.nordstrom.hydrogen.consumer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationConfig {

  @Value("${ENVIRONMENT}")
  private String environment;
  
  @Value("${SOURCE_S3_ENVIRONMENT}")
  private String sourceS3Environment;

  @Value("${SQS_URL}")
  private String sqsUrl;

  @Autowired
  public KafkaConfig kafkaConfig;

  @Autowired
  public LoggingConfig loggingConfig;

  @Autowired
  public SolrConfig solrConfig;

  @Autowired
  public S3Config s3Config;

  public String getEnvironment() {
    return environment;
  }

  public String getSourceS3Environment() {
    return sourceS3Environment;
  }

  public String getSqsUrl() {
    return sqsUrl;
  }

  public String getKafkaBootstrapServers() {
    return this.kafkaConfig.getBootstrapServers();
  }

  public String getKafkaGroupId() {
    return this.kafkaConfig.getGroupId();
  }

  public String getKafkaSkuTopic() {
    return this.kafkaConfig.getKafkaTopic();
  }

  public int getNumberOfConsumersPerNode() {
    return this.kafkaConfig.getNumberOfConsumersPerNode();
  }

  public int getKafkaPollIntervalMs() {
    return this.kafkaConfig.getPollIntervalMs();
  }

  public int getNumberOfThreadsPerConsumer() {
    return this.kafkaConfig.getNumberOfThreadsPerConsumer();
  }

  public String getSolrDeployMarkerFilePath() {
    return this.solrConfig.getSolrDeployMarkerFilePath();
  }
  
  public int getMaxPollRecords() {
    return this.kafkaConfig.getMaxPollRecords();
  }

  public String getLoggingApplicationId() {
    return this.loggingConfig.getLoggingApplicationId();
  }

  public String getLoggingNamespace() {
    return this.loggingConfig.getLoggingNamespace();
  }

  public int getIngestionBatchSize() {
    return this.solrConfig.getIngestionBatchSize();
  }

  public int getIngestionDeleteBatchSize() {
    return this.solrConfig.getIngestionDeleteBatchSize();
  }

  public String getHost() {
    return this.solrConfig.getHost();
  }

  public int getS3ReadBatchSize() {
    return this.s3Config.getS3ReadBatchSize();
  }

}
