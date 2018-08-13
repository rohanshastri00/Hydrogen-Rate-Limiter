package com.nordstrom.hydrogen.consumer.model;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class MetricSpec {

    @JsonProperty
    private String query;

    @JsonProperty
    private Map<String, Map<String, Double> > threshold;

    @JsonProperty
    private String unit;

    @JsonProperty
    private String provider;

    @JsonProperty
    private Double minsAgo;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Map<String, Double>> getThreshold() {
        return threshold;
    }

    public void setThreshold(Map<String, Map<String, Double>> threshold) {
        this.threshold = threshold;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Double getMinsAgo() {
        return minsAgo;
    }

    public void setMinsAgo(Double minsAgo) {
        this.minsAgo = minsAgo;
    }

}
