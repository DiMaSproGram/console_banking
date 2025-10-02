package org.example.command;

import org.example.dto.Account;
import org.example.exception.UserNotFoundException;
import org.example.service.AccountService;

import java.util.Scanner;

public class CreateAccountCommand extends Command {

    private AccountService accountService;
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

        Account account = null;
        try {
            account = accountService.createAccount(userId);
        } catch (UserNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.printf("Account with balance '%s' created!\n", account.getMoneyAmount());
    }
}
