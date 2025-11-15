package org.example.repository;

import org.example.model.entity.Product;
import org.example.repository.enumPath.StoragePath;

import java.util.HashMap;

public interface ProductRepository {
    HashMap<Long, Product> getProductMap();

    void saveProduct(HashMap<Long, Product> map, StoragePath storagePath);

    HashMap<Long, Product> loadMapProduct(StoragePath storagePath);

    void setProductMap(HashMap<Long, Product> productMap);

}
