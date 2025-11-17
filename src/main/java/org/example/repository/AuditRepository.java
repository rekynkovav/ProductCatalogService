package org.example.repository;

import org.example.model.entity.Product;
import org.example.model.entity.User;

import java.util.HashMap;

public interface AuditRepository {
    void setPopularProducts(long id);

    HashMap<Long, Product> getMapPopularProducts();

    HashMap<String, User> getMapRequestUser();

    void auditUserAddBasket(String name);

    void setMapPopularProducts(HashMap<Long, Product> map);

    void setMapRequestUser(HashMap<String, User> map);
}
