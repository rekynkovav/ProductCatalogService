package org.example.service.impl;

import org.example.config.UserSecurityConfig;
import org.example.config.impl.UserSecurityConfigImpl;
import org.example.model.entity.Product;
import org.example.model.entity.User;
import org.example.repository.impl.AuditRepositoryImpl;
import org.example.service.AuditService;

import java.util.HashMap;

public class AuditServiceImpl implements AuditService {

    private static AuditServiceImpl auditService;

    public static AuditServiceImpl getInstance(){
        if (auditService == null) {
            auditService = new AuditServiceImpl();
        }
        return auditService;
    }

    private AuditServiceImpl(){

    }

    private AuditRepositoryImpl auditRepository = AuditRepositoryImpl.getInstance();

    private UserSecurityConfig userSecurityConfig = UserSecurityConfigImpl.getInstance();

    @Override
    public HashMap<Long, Product> getPopularProductsMap() {
        return auditRepository.getMapPopularProducts();
    }

    @Override
    public HashMap<String, User> getRequestUserMap() {
        return auditRepository.getMapRequestUser();
    }
}
