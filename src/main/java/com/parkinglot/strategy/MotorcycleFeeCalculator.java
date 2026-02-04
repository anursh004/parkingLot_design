package com.parkinglot.strategy;

import java.time.Duration;
import org.springframework.stereotype.Component;

@Component("motorcycleFeeCalculator")
public class MotorcycleFeeCalculator implements FeeCalculator {
    @Override
    public double calculateFee(Duration duration) {
        long hours = Math.max(1, duration.toHours());
        return hours * 2.5;
    }
}
