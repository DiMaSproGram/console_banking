package org.example.exception;

public class ValidationException extends BankBusinessException {
    public ValidationException(String s) {
        super(s);
    }
}
