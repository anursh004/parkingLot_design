package com.parkinglot.domain;

public record Car(String licensePlate) implements Vehicle {
    @Override
    public VehicleType type() {
        return VehicleType.CAR;
    }
}
