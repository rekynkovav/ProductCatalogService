package org.example.service.impl;

import org.example.model.entity.User;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.UserService;

public class UserServiceImpl implements UserService {

    private UserRepositoryImpl userRepository;
    public void setUserRepository(UserRepositoryImpl userRepository) {
        this.userRepository = userRepository;
    }

    {
        userRepository = new UserRepositoryImpl();
    }

    @Override
    public void addUser(User user) {
        userRepository.addUser(user);
    }

    @Override
    public boolean isContainsUser(String userName) {
        return userRepository.isContainsUser(userName);
    }

    @Override
    public User getUserForName(String userName) {
        return userRepository.getUserMap().get(userName);
    }
}
