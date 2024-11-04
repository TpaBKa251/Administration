package ru.tpu.hostel.administration.exception;

public class BalanceNotFound extends RuntimeException {
    public BalanceNotFound(String message) {
        super(message);
    }
}
