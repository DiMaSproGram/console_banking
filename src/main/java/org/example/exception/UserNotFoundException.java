package org.example.exception;

public class UserNotFoundException extends BankBusinessException {
    public UserNotFoundException(long id) {
        super("User with ID " + id + " not found.");
    }
}
