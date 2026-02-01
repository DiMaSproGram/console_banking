package org.example.service;

import org.example.dto.Account;
import org.example.dto.User;
import org.example.exception.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountService {

    private final UserService userService;
    private final SessionFactory sessionFactory;

    @Value("${account.default-amount}")
    private int defaultAmount;

    @Value("${account.transfer-commission}")
    private double transferCommission;

    public AccountService(UserService userService, SessionFactory sessionFactory) {
        this.userService = userService;
        this.sessionFactory = sessionFactory;
    }

    public Account createAccount(long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User user = userService.findUserById(userId);
                session.merge(user);

                Account account = new Account(BigDecimal.valueOf(defaultAmount), user);
                session.persist(account);

                user.addAccount(account);

                tx.commit();
                return account;
            } catch (RuntimeException ex) {
                safeRollback(tx);
                throw (ex instanceof BankBusinessException)
                        ? ex
                        : new BankBusinessException("Unable to create new account for user: " + userId);
            }
        }
    }

    public Account findAccountById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Account account = session.get(Account.class, id);
            if (account == null) {
                throw new AccountNotFoundException(id);
            }
            return account;
        }
    }

    public void deposit(long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Deposit amount must be positive");
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Account account = findAccountById(accountId);
                account.setMoneyAmount(account.getMoneyAmount().add(amount));
                session.merge(account);

                tx.commit();
            } catch (RuntimeException ex) {
                safeRollback(tx);
                throw (ex instanceof BankBusinessException)
                        ? ex
                        : new BankBusinessException("Unable to deposit to: " + accountId);
            }
        }
    }

    public void withdraw(long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Withdrawal amount must be positive.");
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Account account = findAccountById(accountId);
                if (isLackOfFunds(account, amount)) {
                    throw new LackOfFundsException(accountId);
                }

                account.setMoneyAmount(account.getMoneyAmount().subtract(amount));
                session.merge(account);

                tx.commit();
            } catch (RuntimeException ex) {
                safeRollback(tx);
                throw (ex instanceof BankBusinessException)
                        ? ex
                        : new BankBusinessException("Unable to withdraw from: " + accountId);
            }
        }
    }

    public void transfer(long fromAccountId, long toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transfer amount must be positive");
        }
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Account fromAccount = findAccountById(fromAccountId);
                Account toAccount = findAccountById(toAccountId);

                if (isLackOfFunds(fromAccount, amount)) {
                    throw new LackOfFundsException(fromAccount.getId());
                }

                BigDecimal commission = BigDecimal.ZERO;
                if (fromAccount.getUser().getId() != toAccount.getUser().getId()) {
                    commission = amount.multiply(BigDecimal.valueOf(transferCommission));
                }

                if (isLackOfFunds(fromAccount, amount.add(commission))) {
                    throw new LackOfFundsException("Lack of funds for transfer with commission");
                }

                fromAccount.setMoneyAmount(fromAccount.getMoneyAmount().subtract(amount.add(commission)));
                toAccount.setMoneyAmount(toAccount.getMoneyAmount().add(amount));
                session.merge(fromAccount);
                session.merge(toAccount);

                tx.commit();
            } catch (RuntimeException ex) {
                safeRollback(tx);
                throw (ex instanceof BankBusinessException)
                        ? ex
                        : new BankBusinessException("Unable to transfer money from: " + fromAccountId + " to: " + toAccountId);
            }
        }
    }

    public void closeAccount(long accountId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Account account = findAccountById(accountId);
                User user = userService.findUserById(account.getUser().getId());
                if (user.getAccountList().size() == 1) {
                    throw new AccountClosingException(user.getLogin());
                }

                transfer(accountId, user.getAccountList().get(0).getId(), account.getMoneyAmount());
                user.removeAccount(account);
                session.remove(account);

                tx.commit();
            } catch (RuntimeException ex) {
                safeRollback(tx);
                throw (ex instanceof BankBusinessException)
                        ? ex
                        : new BankBusinessException("Unable to close account: " + accountId);
            }
        }
    }

    private boolean isLackOfFunds(Account account, BigDecimal amount) {
        return account.getMoneyAmount().compareTo(amount) < 0;
    }

    private static void safeRollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            try { tx.rollback(); } catch (RuntimeException ignored) {}
        }
    }
}
