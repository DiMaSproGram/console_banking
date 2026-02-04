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
import java.util.function.Consumer;
import java.util.function.Function;

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
        return inTransaction(session -> {
            User user = userService.findUserById(userId);
            session.merge(user);

            Account newAccount = new Account(BigDecimal.valueOf(defaultAmount), user);
            session.persist(newAccount);

            user.addAccount(newAccount);
            return newAccount;
        });
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
        inTransaction(session -> {
            Account account = findAccountById(accountId);
            account.setMoneyAmount(account.getMoneyAmount().add(amount));
            session.merge(account);
        });
    }

    public void withdraw(long accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Withdrawal amount must be positive.");
        }
        inTransaction(session -> {
            Account account = findAccountById(accountId);
            if (isLackOfFunds(account, amount)) {
                throw new LackOfFundsException(accountId);
            }

            account.setMoneyAmount(account.getMoneyAmount().subtract(amount));
            session.merge(account);
        });
    }

    public void transfer(long fromAccountId, long toAccountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Transfer amount must be positive");
        }
        inTransaction(session -> {
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
        });
    }

    public void closeAccount(long accountId) {
        inTransaction(session -> {
            Account account = findAccountById(accountId);
            User user = userService.findUserById(account.getUser().getId());
            if (user.getAccountList().size() == 1) {
                throw new AccountClosingException(user.getLogin());
            }

            transfer(accountId, user.getAccountList().get(0).getId(), account.getMoneyAmount());
            user.removeAccount(account);
            session.remove(account);
        });
    }

    private boolean isLackOfFunds(Account account, BigDecimal amount) {
        return account.getMoneyAmount().compareTo(amount) < 0;
    }

    private <T> T inTransaction(Function<Session, T> work) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = work.apply(session);
                tx.commit();
                return result;
            } catch (RuntimeException e) {
                if (tx.isActive()) tx.rollback();
                throw e;
            }
        }
    }

    private void inTransaction(Consumer<Session> work) {
        inTransaction(s -> {
            work.accept(s);
            return null;
        });
    }
}
