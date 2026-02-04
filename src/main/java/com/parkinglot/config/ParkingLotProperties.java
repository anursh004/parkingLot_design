package com.parkinglot.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "parkinglot")
public class ParkingLotProperties {
    private final Capacity capacity = new Capacity();
    private final Otp otp = new Otp();

    public Capacity getCapacity() {
        return capacity;
    }

    public Otp getOtp() {
        return otp;
    }

    public static class Capacity {
        @Min(1)
        private int compact;
        @Min(1)
        private int large;
        @Min(0)
        private int electric;

        public int getCompact() {
            return compact;
        }

        public void setCompact(int compact) {
            this.compact = compact;
        }

        public int getLarge() {
            return large;
        }

        public void setLarge(int large) {
            this.large = large;
        }

        public int getElectric() {
            return electric;
        }

        public void setElectric(int electric) {
            this.electric = electric;
        }
    }

    public static class Otp {
        @Min(60)
        private int ttlSeconds;
        @Min(1)
        private int allowedAttempts;

        public int getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(int ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }

        public int getAllowedAttempts() {
            return allowedAttempts;
        }

        public void setAllowedAttempts(int allowedAttempts) {
            this.allowedAttempts = allowedAttempts;
        }
    }
}
