package org.example.dto;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Account> accountList;

    public User() {
    }

    public User(String login, List<Account> accountList) {
        this.login = login;
        this.accountList = accountList;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void addAccount(Account account) {
        this.accountList.add(account);
    }
    public void removeAccount(Account account) {
        this.accountList.remove(account);
    }
}
