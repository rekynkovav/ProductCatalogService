package org.example.repository.impl;

import org.example.model.entity.User;
import org.example.repository.UserRepository;
import org.example.repository.enumPath.StoragePath;
import org.example.service.impl.UserServiceImpl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class UserRepositoryImpl implements UserRepository {

    private static UserRepositoryImpl userRepository;

    public static UserRepositoryImpl getInstance(){
        if (userRepository == null) {
            userRepository = new UserRepositoryImpl();
        }
        return userRepository;
    }

    private UserRepositoryImpl(){

    }


    private HashMap<String, User> userMap;

    {
        userMap = new HashMap<>();
    }

    @Override
    public HashMap<String, User> getUserMap() {
        return userMap;
    }

    @Override
    public void setUserMap(HashMap<String, User> userMap) {
        this.userMap = userMap;
    }

    @Override
    public void addUser(User user) {
        userMap.put(user.getUserName(), user);
    }

    @Override
    public boolean isContainsUser(String name, String password) {
        return userMap.containsKey(name) && userMap.get(name).getPassword().equals(password);
    }

    @Override
    public boolean isContainsUser(String name) {
        return userMap.containsKey(name);
    }

    @Override
    public void saveUser(HashMap<String, User> map, StoragePath storagePath) {
        String filePath = storagePath.getPath();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            handleSaveException(e, filePath);
        }
    }

    private void handleSaveException(IOException e, String filePath) {
        String errorMessage = String.format(
                "Не удалось сохранить данные в файл: %s. Причина: %s",
                filePath, e.getMessage()
        );
        System.out.println(errorMessage);
    }

    @Override
    public HashMap<String, User> loadMapUser(StoragePath storagePath) {
        String filepath = storagePath.getPath();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filepath))) {

            Object obj = objectInputStream.readObject();

            if (obj instanceof HashMap) {
                return (HashMap<String, User>) obj;
            } else {
                System.out.println("В файле находится не HashMap: " + obj.getClass().getSimpleName());
                return new HashMap<>();
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Ошибка при десериализации файла " + filepath + ": " + e.getMessage());
            return new HashMap<>();
        }
    }
}
