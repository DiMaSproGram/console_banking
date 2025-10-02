package org.example.exception;

public class LoginAlreadyExistsException extends BankBusinessException {
    public LoginAlreadyExistsException(String login) {
        super("User with login '" + login + "' already exists.");
    }
}
