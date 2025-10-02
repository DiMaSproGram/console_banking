package org.example.exception;

public class AccountNotFoundException extends BankBusinessException {
    public AccountNotFoundException(int id) {
        super("Account with ID " + id + " not found");
    }
}
