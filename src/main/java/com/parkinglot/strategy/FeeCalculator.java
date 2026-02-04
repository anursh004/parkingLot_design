package com.parkinglot.strategy;

import java.time.Duration;

public interface FeeCalculator {
    double calculateFee(Duration duration);
}
