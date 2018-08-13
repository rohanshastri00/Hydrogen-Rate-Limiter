package com.nordstrom.hydrogen.consumer.dependency;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nordstrom.hydrogen.consumer.domain.solr.MetricServiceFactory;
import com.nordstrom.hydrogen.consumer.model.MetricSpec;
import com.nordstrom.hydrogen.consumer.utility.ResourceFileReader;
import com.nordstrom.sharedlib.utility.YamlHelper;

@Configuration
public class MetricsBeanBootstrapper {

    @Autowired
    private YamlHelper yamlHelper;

    @Autowired
    private ResourceFileReader resourceFileReader;

    private static final String METRIC_FILE_NAME = "metric.yml";

    @Bean
    public MetricServiceFactory createMetricServiceFactory() {
        return new MetricServiceFactory();
    }

    @Bean
    public List<MetricSpec> createMetricSpecs() {
        String configString = this.resourceFileReader.readFileToString(METRIC_FILE_NAME);
        return this.yamlHelper.deserializeYamlToList(configString, MetricSpec[].class);
    }
}
