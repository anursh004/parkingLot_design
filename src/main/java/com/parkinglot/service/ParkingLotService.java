package com.parkinglot.service;

import com.parkinglot.config.ParkingLotProperties;
import com.parkinglot.domain.ParkingSpotType;
import com.parkinglot.domain.ParkingTicket;
import com.parkinglot.domain.Vehicle;
import com.parkinglot.domain.VehicleFactory;
import com.parkinglot.domain.VehicleType;
import com.parkinglot.exception.ParkingLotFullException;
import com.parkinglot.exception.TicketNotFoundException;
import com.parkinglot.strategy.FeeCalculatorFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ParkingLotService {
    private static final Logger logger = LoggerFactory.getLogger(ParkingLotService.class);

    private final TicketRepository ticketRepository;
    private final VehicleFactory vehicleFactory;
    private final FeeCalculatorFactory feeCalculatorFactory;
    private final ParkingLotProperties properties;

    public ParkingLotService(
            TicketRepository ticketRepository,
            VehicleFactory vehicleFactory,
            FeeCalculatorFactory feeCalculatorFactory,
            ParkingLotProperties properties
    ) {
        this.ticketRepository = ticketRepository;
        this.vehicleFactory = vehicleFactory;
        this.feeCalculatorFactory = feeCalculatorFactory;
        this.properties = properties;
    }

    public ParkingTicket parkVehicle(VehicleType type, String licensePlate) {
        Vehicle vehicle = vehicleFactory.create(type, licensePlate);
        ParkingSpotType spotType = assignSpot(vehicle.type());
        ensureCapacity(spotType);

        ParkingTicket ticket = new ParkingTicket(
                UUID.randomUUID().toString(),
                vehicle.licensePlate(),
                vehicle.type(),
                spotType,
                Instant.now()
        );
        ticketRepository.save(ticket);
        logger.info("Vehicle parked. TicketId={}, plate={}, spotType={}",
                ticket.ticketId(), ticket.licensePlate(), ticket.spotType());
        return ticket;
    }

    public ExitReceipt exitVehicle(String ticketId) {
        ParkingTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found."));
        Duration duration = Duration.between(ticket.entryTime(), Instant.now());
        double fee = feeCalculatorFactory.getCalculator(ticket.vehicleType()).calculateFee(duration);
        ticketRepository.deleteById(ticketId);
        logger.info("Vehicle exited. TicketId={}, fee={}", ticket.ticketId(), fee);
        return new ExitReceipt(ticket.ticketId(), ticket.licensePlate(), fee, duration.toMinutes());
    }

    public CapacitySnapshot capacitySnapshot() {
        long compactUsed = ticketRepository.countBySpotType(ParkingSpotType.COMPACT);
        long largeUsed = ticketRepository.countBySpotType(ParkingSpotType.LARGE);
        long electricUsed = ticketRepository.countBySpotType(ParkingSpotType.ELECTRIC);
        return new CapacitySnapshot(
                properties.getCapacity().getCompact(),
                compactUsed,
                properties.getCapacity().getLarge(),
                largeUsed,
                properties.getCapacity().getElectric(),
                electricUsed
        );
    }

    private ParkingSpotType assignSpot(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> ParkingSpotType.COMPACT;
            case CAR -> ParkingSpotType.LARGE;
            case ELECTRIC -> ParkingSpotType.ELECTRIC;
        };
    }

    private void ensureCapacity(ParkingSpotType spotType) {
        long used = ticketRepository.countBySpotType(spotType);
        int capacity = switch (spotType) {
            case COMPACT -> properties.getCapacity().getCompact();
            case LARGE -> properties.getCapacity().getLarge();
            case ELECTRIC -> properties.getCapacity().getElectric();
        };
        if (used >= capacity) {
            throw new ParkingLotFullException("No available spots for " + spotType + ".");
        }
    }

    public record ExitReceipt(String ticketId, String licensePlate, double fee, long durationMinutes) {
    }

    public record CapacitySnapshot(
            int compactTotal,
            long compactUsed,
            int largeTotal,
            long largeUsed,
            int electricTotal,
            long electricUsed
    ) {
    }
}
