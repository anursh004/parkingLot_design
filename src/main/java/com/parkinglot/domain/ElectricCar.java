package com.parkinglot.domain;

public record ElectricCar(String licensePlate) implements Vehicle {
    @Override
    public VehicleType type() {
        return VehicleType.ELECTRIC;
    }
}
