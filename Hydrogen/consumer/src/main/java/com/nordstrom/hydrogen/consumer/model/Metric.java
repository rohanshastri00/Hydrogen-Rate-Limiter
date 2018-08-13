package com.nordstrom.hydrogen.consumer.model;

public class Metric {

    private String name;
    private Double value;
    private String description;

    public Metric(String name, Double value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
