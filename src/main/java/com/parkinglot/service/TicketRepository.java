package com.parkinglot.service;

import com.parkinglot.domain.ParkingSpotType;
import com.parkinglot.domain.ParkingTicket;
import java.util.Optional;

public interface TicketRepository {
    ParkingTicket save(ParkingTicket ticket);

    Optional<ParkingTicket> findById(String ticketId);

    void deleteById(String ticketId);

    long countBySpotType(ParkingSpotType spotType);
}
