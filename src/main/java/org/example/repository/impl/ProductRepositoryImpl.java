package org.example.repository.impl;

import org.example.model.entity.Product;
import org.example.repository.ProductRepository;
import org.example.repository.enumPath.StoragePath;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class ProductRepositoryImpl implements ProductRepository {
    private HashMap<Long, Product> productMap;

    {
        productMap = new HashMap<>(50);
    }

    @Override
    public HashMap<Long, Product> getProductMap() {
        return productMap;
    }

    @Override
    public void setProductMap(HashMap<Long, Product> productMap) {
        this.productMap = productMap;
    }

    @Override
    public void saveProduct(HashMap<Long, Product> map, StoragePath storagePath) {
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
    public HashMap<Long, Product> loadMapProduct(StoragePath storagePath) {
        String filepath = storagePath.getPath();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filepath))) {

            Object obj = objectInputStream.readObject();

            if (obj instanceof HashMap) {
                return (HashMap<Long, Product>) obj;
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
