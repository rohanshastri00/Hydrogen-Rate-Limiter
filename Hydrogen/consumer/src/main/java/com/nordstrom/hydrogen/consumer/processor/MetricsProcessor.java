package com.nordstrom.hydrogen.consumer.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nordstrom.hydrogen.consumer.domain.solr.MetricServiceFactory;
import com.nordstrom.hydrogen.consumer.model.Metric;
import com.nordstrom.hydrogen.consumer.model.MetricSpec;
import com.nordstrom.hydrogen.consumer.model.RiskLevel;
import com.nordstrom.hydrogen.consumer.service.MetricService;
import com.nordstrom.sharedlib.logging.ApplicationLogger;

@Component
public class MetricsProcessor {

    @Autowired
    private List<MetricSpec> metricSpecs;

    @Autowired
    private MetricServiceFactory metricServiceFactory;

    @Autowired
    private ApplicationLogger logger;

    public RiskLevel getRiskLevel() {
        List<RiskLevel> riskLevelList = new ArrayList<>();

        try {
            for (MetricSpec metricSpec : metricSpecs) {
                riskLevelList.add(determineRiskLevel(metricSpec.getThreshold(), processMetrics(metricSpec)));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return RiskLevel.GOOD;
        }

        if (riskLevelList.contains(RiskLevel.BAD)) {
            return RiskLevel.BAD;
        } else if (riskLevelList.contains(RiskLevel.MEDIUM)) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.GOOD;
    }

    private Metric processMetrics(MetricSpec metricSpec) {

        MetricService metricService = metricServiceFactory.getService(metricSpec.getProvider());
        try {
            return metricService.execute(metricSpec);
        } catch (Exception ex) {
            logger.error(ex, "error processing metrics");
        }
        return null;
    }


    private RiskLevel determineRiskLevel(
            Map<String, Map<String, Double>> threshold,
            Metric metric) {
        try {
            if (checkRiskLevel(threshold, metric.getValue(), RiskLevel.MEDIUM)) {
                return RiskLevel.MEDIUM;
            } else if (checkRiskLevel(threshold, metric.getValue(), RiskLevel.BAD)) {
                return RiskLevel.BAD;
            }
        } catch (Exception ex) {
            logger.error (ex.getMessage());
            return RiskLevel.GOOD;
        }
        return RiskLevel.GOOD;
    }

    private boolean checkRiskLevel(
            Map<String, Map<String, Double>> threshold,
            double calculatedValue,
            RiskLevel riskLevel) {
        String tier = riskLevel.toString().toLowerCase();
        return calculatedValue >= threshold.get(tier).get("low")
                && calculatedValue <= threshold.get(tier).get("high");

    }


}
