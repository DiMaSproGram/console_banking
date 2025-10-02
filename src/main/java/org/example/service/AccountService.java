package org.example.service;

import org.example.dto.Account;
import org.example.dto.User;
import org.example.exception.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AccountService {
    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    private final UserService userService;

    @Value("${account.default-amount}")
    private int defaultAmount;

    @Value("${account.transfer-commission}")
    private double transferCommission;

    public AccountService(UserService userService) {
        this.userService = userService;
    }

    public Account createAccount(int userId) {
        User user = userService.findUserById(userId);
        Account newAccount = new Account(idGenerator.incrementAndGet(), userId, BigDecimal.valueOf(defaultAmount));
        accounts.put(idGenerator.get(), newAccount);
        user.addAccount(newAccount);
        return newAccount;
    }

    public Account findAccountById(int id) {
        Account account = accounts.get(id);
        if (account == null) {
            throw new AccountNotFoundException(id);
        }
        return account;
    }

    public void deposit(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Deposit amount must be positive");
        }
        Account account = findAccountById(accountId);
        account.setMoneyAmount(account.getMoneyAmount().add(amount));
    }

    public void withdraw(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Withdrawal amount must be positive.");
        }
        Account account = findAccountById(accountId);
        if (isLackOfFunds(account, amount)) {
            throw new LackOfFundsException(accountId);
        }
        account.setMoneyAmount(account.getMoneyAmount().subtract(amount));
    }

    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transfer amount must be positive");
        }
        Account fromAccount = findAccountById(fromAccountId);
        Account toAccount = findAccountById(toAccountId);

        if (isLackOfFunds(fromAccount, amount)) {
            throw new LackOfFundsException(fromAccount.getId());
        }

        BigDecimal commission = new BigDecimal(0);
        if (fromAccount.getUserId() != toAccount.getUserId()) {
            commission = amount.multiply(BigDecimal.valueOf(transferCommission));
        }

        if (isLackOfFunds(fromAccount, amount.add(commission))) {
            throw new LackOfFundsException("Lack of funds for transfer with commission");
        }

        fromAccount.setMoneyAmount(fromAccount.getMoneyAmount().subtract(amount.add(commission)));
        toAccount.setMoneyAmount(toAccount.getMoneyAmount().add(amount));
    }

    public void closeAccount(int accountId) {
        Account account = findAccountById(accountId);
        User user = userService.findUserById(account.getUserId());
        if (user.accounts().size() == 1) {
            throw new AccountClosingException(user.login());
        }

        transfer(accountId, user.accounts().get(0).getId(), account.getMoneyAmount());
        accounts.remove(accountId);
        user.removeAccount(account);
    }

    private boolean isLackOfFunds(Account account, BigDecimal amount) {
        return account.getMoneyAmount().compareTo(amount) < 0;
    }
}
