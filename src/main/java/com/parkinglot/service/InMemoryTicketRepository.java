package com.parkinglot.service;

import com.parkinglot.domain.ParkingSpotType;
import com.parkinglot.domain.ParkingTicket;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTicketRepository implements TicketRepository {
    private final Map<String, ParkingTicket> store = new ConcurrentHashMap<>();

    @Override
    public ParkingTicket save(ParkingTicket ticket) {
        store.put(ticket.ticketId(), ticket);
        return ticket;
    }

    @Override
    public Optional<ParkingTicket> findById(String ticketId) {
        return Optional.ofNullable(store.get(ticketId));
    }

    @Override
    public void deleteById(String ticketId) {
        store.remove(ticketId);
    }

    @Override
    public long countBySpotType(ParkingSpotType spotType) {
        return store.values().stream()
                .filter(ticket -> ticket.spotType() == spotType)
                .count();
    }
}
