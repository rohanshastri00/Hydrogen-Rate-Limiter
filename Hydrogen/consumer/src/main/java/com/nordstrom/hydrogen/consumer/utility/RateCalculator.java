package com.nordstrom.hydrogen.consumer.utility;

import com.nordstrom.hydrogen.consumer.model.RiskLevel;

public class RateCalculator {

    public double calculateRate (RiskLevel riskLevel) {
        if (riskLevel.equals(RiskLevel.MEDIUM)) {
            return 4.0;
        } else if (riskLevel.equals(RiskLevel.BAD)) {
            return 0.0;
        }
        return 5.0;
    }
}
