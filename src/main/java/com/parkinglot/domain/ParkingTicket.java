package com.parkinglot.domain;

import java.time.Instant;

public record ParkingTicket(
        String ticketId,
        String licensePlate,
        VehicleType vehicleType,
        ParkingSpotType spotType,
        Instant entryTime
) {
}
