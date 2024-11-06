package ru.tpu.hostel.administration.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.tpu.hostel.administration.exception.BalanceNotFound;
import ru.tpu.hostel.administration.exception.DocumentNotFound;
import ru.tpu.hostel.administration.exception.UserNotFound;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFound ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "404");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DocumentNotFound.class)
    public ResponseEntity<Map<String, String>> handleDocumentNotFound(DocumentNotFound ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "404");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BalanceNotFound.class)
    public ResponseEntity<Map<String, String>> handleBalanceNotFound(BalanceNotFound ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "404");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "409");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "400");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "500");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
