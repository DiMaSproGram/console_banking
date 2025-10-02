package org.example.exception;

public class AccountClosingException extends BankBusinessException {
    public AccountClosingException(String login) {
        super("Account that belongs to the user " + login + " can't be closed, " +
                "because it main and only");
    }
}
