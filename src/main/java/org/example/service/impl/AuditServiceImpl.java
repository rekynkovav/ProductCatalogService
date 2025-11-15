package org.example.service.impl;

import org.example.config.UserSecurityConfig;
import org.example.model.entity.Product;
import org.example.model.entity.User;
import org.example.repository.impl.AuditRepositoryImpl;
import org.example.service.AuditService;

import java.util.HashMap;

public class AuditServiceImpl implements AuditService {
    private AuditRepositoryImpl repository;

    public void setUserSecurityConfig(UserSecurityConfig userSecurityConfig) {
        this.userSecurityConfig = userSecurityConfig;
    }

    private UserSecurityConfig userSecurityConfig;

    public void setRepository(AuditRepositoryImpl repository) {
        this.repository = repository;
    }

    {
        repository = new AuditRepositoryImpl();
    }

    @Override
    public HashMap<Long, Product> getPopularProductsMap() {
        return repository.getMapPopularProducts();
    }

    @Override
    public HashMap<String, User> getRequestUserMap() {
        return repository.getMapRequestUser();
    }
}
