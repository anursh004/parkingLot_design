package com.parkinglot.strategy;

import com.parkinglot.domain.VehicleType;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FeeCalculatorFactory {
    private final Map<VehicleType, FeeCalculator> calculators = new EnumMap<>(VehicleType.class);

    public FeeCalculatorFactory(
            CarFeeCalculator carFeeCalculator,
            MotorcycleFeeCalculator motorcycleFeeCalculator,
            ElectricFeeCalculator electricFeeCalculator
    ) {
        calculators.put(VehicleType.CAR, carFeeCalculator);
        calculators.put(VehicleType.MOTORCYCLE, motorcycleFeeCalculator);
        calculators.put(VehicleType.ELECTRIC, electricFeeCalculator);
    }

    public FeeCalculator getCalculator(VehicleType vehicleType) {
        return calculators.get(vehicleType);
    }
}
