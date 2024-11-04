package ru.tpu.hostel.administration.exception;

public class DocumentNotFound extends RuntimeException {
    public DocumentNotFound(String message) {
        super(message);
    }
}
