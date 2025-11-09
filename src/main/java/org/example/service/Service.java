package org.example.service;

import org.example.entity.Role;
import org.example.entity.User;

import java.util.HashMap;

/**
 * security класс для предоставления доступа к данным магазина
 * содержит мапу пользователей
 * метод registerUser для регистрации новых пользователей
 * метод checkUser для проверки зарегистрированного пользователя в базе
 * метод getThisUser для доступа к текущему авторизованному пользователю
 */
public class Service {

    private HashMap<String, User> userMap = new HashMap<>();
    private static User thisUser;

    public HashMap<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(HashMap<String, User> userMap) {
        this.userMap = userMap;
    }

    public void registerUser(String userName, String password, Role role) {
        User user = new User(userName, password, role);
        userMap.put(userName, user);
        thisUser = userMap.get(userName);
        thisUser.setIn(thisUser.getIn() + 1);
    }

    public boolean checkUser(String userName, String password) {
        if (userMap.containsKey(userName) && userMap.get(userName).getPassword().equals(password)) {
            thisUser = userMap.get(userName);
            thisUser.setIn(thisUser.getIn() + 1);
            return true;
        }
        System.out.println("Hе верный пароль");
        return false;
    }

    public static User getThisUser() {
        return thisUser;
    }
}
