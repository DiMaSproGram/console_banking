package org.example.command;

import org.example.dto.User;
import org.example.service.UserService;

import java.util.List;

public class ShowAllUsersCommand extends Command {

    private final UserService userService;

    public ShowAllUsersCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void execute() {
        List<User> users = userService.findAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users found");
            return;
        }
        users.forEach(System.out::println);
    }
}
