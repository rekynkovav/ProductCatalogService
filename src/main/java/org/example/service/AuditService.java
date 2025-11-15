package org.example.service;

import org.example.model.entity.Product;
import org.example.model.entity.User;

import java.util.HashMap;

public interface AuditService {
    HashMap<Long, Product> getPopularProductsMap();
    HashMap<String, User> getRequestUserMap();
}
