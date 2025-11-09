package org.example.repository;

import org.example.entity.Product;
import org.example.entity.User;

import java.io.*;
import java.util.HashMap;

/**
 * класс для сериализации и десериализации
 * всех товаров магазина
 * пользователях
 * действиях пользователей
 */
public class Repository {

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
            Product.setId(map.size());
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

    public static void saveRequests(HashMap<Long, Product> map) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\requests.txt"));) {
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Long, Product> loadRequests() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\requests.txt"));) {
            HashMap<Long, Product> map = (HashMap<Long, Product>) objectInputStream.readObject();
            return map;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("При десериализации бизнес информации возникла ошибка");
            return new HashMap<>();
        }
    }

    public static void saveBusinessInfoUser(HashMap<String, User> map) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\businessInfoUser.txt"));) {
            objectOutputStream.writeObject(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<String, User> loadBusinessInfoUser() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("C:\\Users\\Suveren\\IdeaProjects\\ProductCatalogService\\src\\main\\resources\\businessInfoUser.txt"));) {
            HashMap<String, User> map = (HashMap<String, User>) objectInputStream.readObject();
            return map;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("При десериализации списка активности юзеров возникла ошибка");
            return new HashMap<>();
        }
    }
}
