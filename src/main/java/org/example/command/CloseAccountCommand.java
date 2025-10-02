package org.example.command;

import org.example.dto.Account;
import org.example.exception.AccountNotFoundException;
import org.example.exception.LackOfFundsException;
import org.example.exception.UserNotFoundException;
import org.example.service.AccountService;

import java.util.Scanner;

public class CloseAccountCommand extends Command {

    private final AccountService accountService;
    private final Scanner scanner;

    public CloseAccountCommand(AccountService accountService) {
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void execute() {
        System.out.println("Enter id of the account:");
        int accountId = scanner.nextInt();
        scanner.nextLine();

        try {
            accountService.closeAccount(accountId);
        } catch (AccountNotFoundException | UserNotFoundException | LackOfFundsException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("Account was closed! ");
    }
}
