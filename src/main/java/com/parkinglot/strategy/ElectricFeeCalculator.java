package com.parkinglot.strategy;

import java.time.Duration;
import org.springframework.stereotype.Component;

@Component("electricFeeCalculator")
public class ElectricFeeCalculator implements FeeCalculator {
    @Override
    public double calculateFee(Duration duration) {
        long hours = Math.max(1, duration.toHours());
        return hours * 4.0;
    }
}
