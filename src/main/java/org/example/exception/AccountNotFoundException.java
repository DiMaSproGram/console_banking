package org.example.exception;

public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String s) {
        super(s);
    }
}
