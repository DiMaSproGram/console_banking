package org.example.exception;

public class BankBusinessException extends RuntimeException {
    public BankBusinessException(String message) {
        super(message);
    }
}
