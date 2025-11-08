package org.example.front;

import org.example.entity.Role;
import org.example.entity.User;

import java.util.HashMap;

public class Front {

    private HashMap<String,User> userMap  = new HashMap<>();

    public HashMap<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(HashMap<String, User> userMap) {
        this.userMap = userMap;
    }

    public void registerUser(String userName, String password, Role role){
        User user = new User(userName, password, role);
        userMap.put(userName, user);
    }

    public boolean checkUser(String userName, String password){
        if(userMap.containsKey(userName) && userMap.get(userName).getPassword().equals(password)){
            return true;
        }
        System.out.println("Hе верный пароль");
        return false;
    }

    public User getUser(String name){
        return userMap.get(name);
    }


}
