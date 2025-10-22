package org.example.command;

import org.example.dto.Account;
import org.example.exception.UserNotFoundException;
import org.example.service.AccountService;

import java.util.Scanner;

public class CreateAccountCommand extends Command {

    private final AccountService accountService;
    private final Scanner scanner;

    public CreateAccountCommand(AccountService accountService) {
        this.accountService = accountService;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void execute() {
        System.out.println("Enter id of the user:");
        int userId = scanner.nextInt();
        scanner.nextLine();

        Account account = accountService.createAccount(userId);

        System.out.printf("Account with balance '%s' created!\n", account.getMoneyAmount());
    }
}
