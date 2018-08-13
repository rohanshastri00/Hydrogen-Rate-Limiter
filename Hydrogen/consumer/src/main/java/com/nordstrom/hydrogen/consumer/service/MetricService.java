package com.nordstrom.hydrogen.consumer.service;

import com.nordstrom.hydrogen.consumer.model.Metric;
import com.nordstrom.hydrogen.consumer.model.MetricSpec;


public interface MetricService {

    public Metric execute(MetricSpec metricSpec) throws Exception;

}
