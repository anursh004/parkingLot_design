package com.parkinglot.exception;

import java.time.Instant;

public record ApiError(String message, Instant timestamp) {
}
