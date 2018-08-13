package com.nordstrom.hydrogen.consumer.domain.solr;

import org.springframework.stereotype.Component;

import com.nordstrom.hydrogen.consumer.service.DatadogService;
import com.nordstrom.hydrogen.consumer.service.MetricService;

@Component
public class MetricServiceFactory {

    public MetricService getService(String provider) {

        if (provider == null) {
            return null;
        }

        if (provider.equalsIgnoreCase("DATADOG")) {
            return new DatadogService();
        }
        return null;

    }
}
