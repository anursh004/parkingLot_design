package com.parkinglot.domain;

public record Motorcycle(String licensePlate) implements Vehicle {
    @Override
    public VehicleType type() {
        return VehicleType.MOTORCYCLE;
    }
}
