package org.example.exception;

public class LackOfFundsException extends BankBusinessException {
  public LackOfFundsException(long accountId) {
    super("Error: lack of funds on account ID " + accountId );
  }

  public LackOfFundsException(String message) {
    super(message);
  }
}

