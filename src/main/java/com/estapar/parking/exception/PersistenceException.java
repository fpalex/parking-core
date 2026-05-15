package com.estapar.parking.exception;

public class PersistenceException extends RuntimeException {
    public PersistenceException(String message) {
        super(message);
    }
}