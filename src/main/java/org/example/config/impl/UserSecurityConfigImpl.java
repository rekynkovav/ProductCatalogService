package org.example.config.impl;

import org.example.config.UserSecurityConfig;
import org.example.model.entity.Role;
import org.example.model.entity.User;
import org.example.repository.impl.UserRepositoryImpl;
import org.example.service.impl.UserServiceImpl;

public class UserSecurityConfigImpl implements UserSecurityConfig {

    private static UserSecurityConfigImpl userSecurityConfig;

    public static UserSecurityConfigImpl getInstance(){
        if (userSecurityConfig == null) {
            userSecurityConfig = new UserSecurityConfigImpl();
        }
        return userSecurityConfig;
    }

    private UserSecurityConfigImpl(){

    }

    private UserRepositoryImpl userRepository = UserRepositoryImpl.getInstance();
    private UserServiceImpl userServise = UserServiceImpl.getInstance();
    private User thisUser;


    public User getThisUser() {
        return thisUser;
    }

    @Override
    public void setThisUser(User thisUser) {
        this.thisUser = thisUser;
    }

    @Override
    public boolean verificationUser(String userName, String password) {
        /**
         * проверяет совпадение пароля,
         * открывает сеессию юзера,
         * сохраняет юзера в переменную текущийЮзер
         */
        if (userRepository.isContainsUser(userName, password)) {
            setThisUser(userRepository.getUserMap().get(userName));
            thisUser.setIn(thisUser.getIn() + 1);
            return true;
        }
        System.out.println("Hе верный пароль");
        return false;
    }

    @Override
    public void registerUser(String userName, String password, Role role) {
        User user = new User(userName, password, role);
        user.setIn(1);
        userServise.addUser(user);
        thisUser = user;
    }

    @Override
    public boolean isAuthenticated() {
        return thisUser != null;
    }
}
