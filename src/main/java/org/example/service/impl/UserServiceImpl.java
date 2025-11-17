package org.example.service.impl;

import org.example.model.entity.User;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.UserService;

public class UserServiceImpl implements UserService {

    private static UserServiceImpl userService;

    public static UserServiceImpl getInstance(){
        if (userService == null) {
            userService = new UserServiceImpl();
        }
        return userService;
    }

    private UserServiceImpl(){

    }

    private static UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();

    @Override
    public void addUser(User user) {
        userRepository.addUser(user);
    }

    @Override
    public boolean isContainsUser(String userName) {
        return userRepository.isContainsUser(userName);
    }

}
