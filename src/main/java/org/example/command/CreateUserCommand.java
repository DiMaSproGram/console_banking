package org.example.command;

import org.example.dto.Account;
import org.example.dto.User;
import org.example.exception.LoginAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.example.service.AccountService;
import org.example.service.UserService;

import java.util.Scanner;

public class CreateUserCommand extends Command {

    private final UserService userService;
    private final AccountService accountService;
    private final Scanner scanner;

    public CreateUserCommand(AccountService accountService, UserService userService) {
        this.scanner = new Scanner(System.in);
        this.accountService = accountService;
        this.userService = userService;
    }

    @Override
    public void execute() {
        System.out.println("Enter user login:" );
        String login = scanner.nextLine();
        while (login.isEmpty()) {
            System.out.println("Enter user login:" );
            login = scanner.nextLine();
        }

        User user;
        try {
            user = userService.createUser(login);
        } catch (LoginAlreadyExistsException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.printf("User with id '%d' created!\n", user.id());

        Account account;
        try {
            account = accountService.createAccount(user.id());
        } catch (UserNotFoundException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.printf("Account with balance '%s' created!\n", account.getMoneyAmount());
    }
}
