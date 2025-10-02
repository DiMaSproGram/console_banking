package org.example.service;

import org.example.command.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommandExecutor {

    private final Map<String, Command> commands = new HashMap<>();

    public CommandExecutor(AccountService accountService, UserService userService) {

        commands.put("USER_CREATE", new CreateUserCommand(accountService, userService));
        commands.put("SHOW_ALL_USERS", new ShowAllUsersCommand(userService));
        commands.put("ACCOUNT_CREATE", new CreateAccountCommand(accountService));
        commands.put("ACCOUNT_CLOSE", new CloseAccountCommand(accountService));
        commands.put("ACCOUNT_DEPOSIT", new DepositAccountCommand(accountService));
        commands.put("ACCOUNT_TRANSFER", new TransferAccountCommand(accountService));
        commands.put("ACCOUNT_WITHDRAW", new WithdrawAccountCommand(accountService));
    }

    public Command getCommand(String commandName) {
        return commands.getOrDefault(commandName, new UnknownCommand());
    }
}
