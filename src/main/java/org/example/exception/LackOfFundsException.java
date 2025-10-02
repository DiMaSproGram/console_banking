package org.example.exception;

public class LackOfFundsException extends BankBusinessException {
  public LackOfFundsException(int accountId) {
    super("Error: lack of funds on account ID " + accountId );
  }

  public LackOfFundsException(String message) {
    super(message);
  }
}

