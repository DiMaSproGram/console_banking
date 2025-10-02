package org.example.command;

import org.example.service.AccountService;

import java.math.BigDecimal;
import java.util.Scanner;

public class TransferAccountCommand extends Command {

    private final AccountService accountService;
    private final Scanner scanner;

    public TransferAccountCommand(AccountService accountService) {
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void execute() {
        System.out.println("Enter sender's account id:");
        int accountFromId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter recipient's account id:");
        int accountToId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter amount of money to transfer:");
        int amount = scanner.nextInt();
        scanner.nextLine();

        accountService.transfer(accountFromId, accountToId, BigDecimal.valueOf(amount));

        System.out.println("Transfer successful!");
    }
}
