package com.parkinglot.api;

import com.parkinglot.domain.VehicleType;
import com.parkinglot.service.OtpService;
import com.parkinglot.service.ParkingLotService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ParkingLotController {
    private final ParkingLotService parkingLotService;
    private final OtpService otpService;

    public ParkingLotController(ParkingLotService parkingLotService, OtpService otpService) {
        this.parkingLotService = parkingLotService;
        this.otpService = otpService;
    }

    @PostMapping("/park")
    public ResponseEntity<ParkResponse> parkVehicle(@Valid @RequestBody ParkRequest request) {
        var ticket = parkingLotService.parkVehicle(request.vehicleType(), request.licensePlate());
        return ResponseEntity.ok(new ParkResponse(
                ticket.ticketId(),
                ticket.licensePlate(),
                ticket.spotType().name(),
                ticket.entryTime().toString()
        ));
    }

    @PostMapping("/exit")
    public ResponseEntity<ParkingLotService.ExitReceipt> exitVehicle(@Valid @RequestBody ExitRequest request) {
        return ResponseEntity.ok(parkingLotService.exitVehicle(request.ticketId()));
    }

    @GetMapping("/capacity")
    public ResponseEntity<ParkingLotService.CapacitySnapshot> capacitySnapshot() {
        return ResponseEntity.ok(parkingLotService.capacitySnapshot());
    }

    @PostMapping("/otp/request")
    public ResponseEntity<OtpService.OtpResponse> requestOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(otpService.requestOtp(request.principal()));
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<Void> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        otpService.verifyOtp(request.principal(), request.otp());
        return ResponseEntity.ok().build();
    }

    public record ParkRequest(@NotNull VehicleType vehicleType, @NotBlank String licensePlate) {
    }

    public record ParkResponse(String ticketId, String licensePlate, String spotType, String entryTime) {
    }

    public record ExitRequest(@NotBlank String ticketId) {
    }

    public record OtpRequest(@NotBlank String principal) {
    }

    public record OtpVerifyRequest(@NotBlank String principal, @NotBlank String otp) {
    }
}
