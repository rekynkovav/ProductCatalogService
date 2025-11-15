package org.example.service;

import org.example.model.entity.User;

public interface UserService {
    void addUser(User user);

    boolean isContainsUser(String userName);

    User getUserForName(String userName);

}
