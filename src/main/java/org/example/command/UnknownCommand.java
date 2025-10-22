package org.example.command;

public class UnknownCommand extends Command {
    @Override
    public void execute() {
        System.out.println("Unknown command");
    }
}
