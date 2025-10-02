package org.example.service;

import org.example.dto.User;
import org.example.exception.LoginAlreadyExistsException;
import org.example.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {

    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    public User createUser(String login) throws LoginAlreadyExistsException {
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            if (entry.getValue().login().equals(login)) {
                throw new LoginAlreadyExistsException(login);
            }
        }

        User newUser = new User(idGenerator.incrementAndGet(), login);
        users.put(idGenerator.get(), newUser);
        return newUser;
    }

    public User findUserById(int id) throws UserNotFoundException {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        return user;
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }
}
