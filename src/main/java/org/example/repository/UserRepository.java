package org.example.repository;

import org.example.model.entity.User;
import org.example.repository.enumPath.StoragePath;

import java.util.HashMap;

public interface UserRepository {
    HashMap<String, User> getUserMap();

    void setUserMap(HashMap<String, User> userMap);

    void addUser(User user);

    boolean isContainsUser(String name, String password);

    boolean isContainsUser(String name);

    void saveUser(HashMap<String, User> map, StoragePath storagePath);

    HashMap<String, User> loadMapUser(StoragePath storagePath);
}
