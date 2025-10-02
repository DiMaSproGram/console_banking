package org.example.exception;

public class LoginAlreadyExistsException extends Exception {
    public LoginAlreadyExistsException(String s) {
        super(s);
    }
}
