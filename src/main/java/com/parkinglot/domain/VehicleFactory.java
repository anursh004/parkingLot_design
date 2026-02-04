package com.parkinglot.domain;

import com.parkinglot.exception.InvalidVehicleException;
import org.springframework.stereotype.Component;

@Component
public class VehicleFactory {
    public Vehicle create(VehicleType type, String licensePlate) {
        if (licensePlate == null || licensePlate.isBlank()) {
            throw new InvalidVehicleException("License plate is required.");
        }
        return switch (type) {
            case CAR -> new Car(licensePlate);
            case MOTORCYCLE -> new Motorcycle(licensePlate);
            case ELECTRIC -> new ElectricCar(licensePlate);
        };
    }
}
