package org.example.command;

import org.example.service.AccountService;

import java.math.BigDecimal;
import java.util.Scanner;

public class WithdrawAccountCommand extends Command {

    private final AccountService accountService;
    private final Scanner scanner;

    public WithdrawAccountCommand(AccountService accountService) {
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void execute() {
        System.out.println("Enter id of the account:");
        int accountId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter amount of money to withdraw:");
        int amount = scanner.nextInt();
        scanner.nextLine();

        accountService.withdraw(accountId, BigDecimal.valueOf(amount));

        System.out.println("Withdraw successful! ");
    }
}
