package org.example.command;

import org.example.service.AccountService;

import java.math.BigDecimal;
import java.util.Scanner;

public class DepositAccountCommand extends Command {

    private final AccountService accountService;
    private final Scanner scanner;

    public DepositAccountCommand(AccountService accountService) {
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void execute() {
        System.out.println("Enter id of the account:");
        int accountId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter amount of money to deposit:");
        int amount = scanner.nextInt();
        scanner.nextLine();

        try {
            accountService.deposit(accountId, BigDecimal.valueOf(amount));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("Deposit successful! ");
    }
}
