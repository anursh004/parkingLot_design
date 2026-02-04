package com.parkinglot.service;

import com.parkinglot.config.ParkingLotProperties;
import com.parkinglot.exception.OtpException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_LENGTH = 6;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, OtpRecord> otpStore = new ConcurrentHashMap<>();
    private final PasswordEncoder passwordEncoder;
    private final ParkingLotProperties properties;

    public OtpService(PasswordEncoder passwordEncoder, ParkingLotProperties properties) {
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    public OtpResponse requestOtp(String principal) {
        String otp = generateOtp();
        Instant expiresAt = Instant.now().plusSeconds(properties.getOtp().getTtlSeconds());
        OtpRecord record = new OtpRecord(passwordEncoder.encode(otp), expiresAt, properties.getOtp().getAllowedAttempts());
        otpStore.put(principal, record);

        logger.info("OTP issued for principal={}, expiresAt={}, otpHint=**{}", principal, expiresAt, otp.substring(4));
        return new OtpResponse(principal, otp, expiresAt.toString());
    }

    public void verifyOtp(String principal, String otp) {
        OtpRecord record = otpStore.get(principal);
        if (record == null) {
            throw new OtpException("OTP not found. Request a new OTP.");
        }
        if (Instant.now().isAfter(record.expiresAt())) {
            otpStore.remove(principal);
            throw new OtpException("OTP expired. Request a new OTP.");
        }
        if (record.remainingAttempts() <= 0) {
            otpStore.remove(principal);
            throw new OtpException("OTP locked. Request a new OTP.");
        }
        if (!passwordEncoder.matches(otp, record.hashedOtp())) {
            record.decrementAttempts();
            logger.warn("Invalid OTP attempt for principal={}, remainingAttempts={}", principal, record.remainingAttempts());
            throw new OtpException("Invalid OTP.");
        }
        otpStore.remove(principal);
        logger.info("OTP verified for principal={}", principal);
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int otp = secureRandom.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }

    public record OtpResponse(String principal, String otp, String expiresAt) {
    }

    private static class OtpRecord {
        private final String hashedOtp;
        private final Instant expiresAt;
        private int remainingAttempts;

        private OtpRecord(String hashedOtp, Instant expiresAt, int remainingAttempts) {
            this.hashedOtp = hashedOtp;
            this.expiresAt = expiresAt;
            this.remainingAttempts = remainingAttempts;
        }

        public String hashedOtp() {
            return hashedOtp;
        }

        public Instant expiresAt() {
            return expiresAt;
        }

        public int remainingAttempts() {
            return remainingAttempts;
        }

        public void decrementAttempts() {
            this.remainingAttempts = Math.max(0, remainingAttempts - 1);
        }
    }
}
