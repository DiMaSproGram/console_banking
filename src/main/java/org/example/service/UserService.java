package org.example.service;

import org.example.dto.User;
import org.example.exception.BankBusinessException;
import org.example.exception.LoginAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {

    private final SessionFactory sessionFactory;

    public UserService(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public User createUser(String login) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                User userFromDB = session.get(User.class, login);
//                Query<User> q = session.createQuery("from User u where u.login = :login", User.class)
//                        .setParameter("login", login);
                if (userFromDB != null) {
                    throw new LoginAlreadyExistsException(login);
                }

                User newUser = new User(login, new ArrayList<>());
                session.persist(newUser);
                tx.commit();
                return newUser;
            } catch (RuntimeException ex) {
                tx.rollback();
                throw (ex instanceof BankBusinessException)
                        ? ex
                        : new BankBusinessException("Unable to create new userr: " + login);
            }
        }
    }

    public User findUserById(long id) throws UserNotFoundException {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new UserNotFoundException(id);
            }
            return user;
        }
    }

    public List<User> findAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User").list();
        }
    }
}
