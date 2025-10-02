package org.example.listener;

import jakarta.annotation.PostConstruct;
import org.example.service.AccountService;
import org.example.service.CommandExecutor;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class OperationsConsoleListener implements Runnable {

    private final CommandExecutor commandExecutor;

    public OperationsConsoleListener(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                showMenu();

                String command = scanner.nextLine().trim().toUpperCase();
                if (command.isEmpty()) {
                    break;
                }
                commandExecutor.getCommand(command).execute();
            }
        }
    }

    private void showMenu() {
        System.out.println("\nPlease enter one of operation type:");
        System.out.println("-ACCOUNT_CREATE");
        System.out.println("-SHOW_ALL_USERS");
        System.out.println("-ACCOUNT_CLOSE");
        System.out.println("-ACCOUNT_WITHDRAW");
        System.out.println("-ACCOUNT_DEPOSIT");
        System.out.println("-ACCOUNT_TRANSFER");
        System.out.println("-USER_CREATE");
    }
}
