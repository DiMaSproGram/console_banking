package org.example.exception;

public class AccountNotFoundException extends BankBusinessException {
    public AccountNotFoundException(long id) {
        super("Account with ID " + id + " not found");
    }
}
