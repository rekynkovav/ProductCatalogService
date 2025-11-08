package org.example.service;

import org.example.entity.Product;
import org.example.entity.User;

import java.io.*;
import java.util.HashMap;

public class Service {
    public static void saveProduct(HashMap<Long, Product> map) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\product.txt"));) {
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Long, Product> loadProduct() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\product.txt"));) {
            HashMap<Long, Product> map = (HashMap<Long, Product>) objectInputStream.readObject();
            return map;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("При десериализации списка продуктов возникла ошибка");
            return new HashMap<>();
        }
    }

    public static void saveUser(HashMap<String, User> map) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\user.txt"));) {
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, User> loadUser() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\user.txt"));) {
            HashMap<String, User> map = (HashMap<String, User>) objectInputStream.readObject();
            return map;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("При десериализации списка юзеров возникла ошибка");
            return new HashMap<>();
        }
    }
}
