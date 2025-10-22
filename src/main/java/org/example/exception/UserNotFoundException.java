package org.example.exception;

public class UserNotFoundException extends BankBusinessException {
    public UserNotFoundException(int id) {
        super("User with ID " + id + " not found.");
    }
}
