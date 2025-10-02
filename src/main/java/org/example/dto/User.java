package org.example.dto;

import java.util.ArrayList;
import java.util.List;

public record User(int id, String login, List<Account> accounts) {
    public User(int id, String login) {
        this(id, login, new ArrayList<>());
    }

    public void addAccount(Account account) {
        accounts.add(account);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }
}