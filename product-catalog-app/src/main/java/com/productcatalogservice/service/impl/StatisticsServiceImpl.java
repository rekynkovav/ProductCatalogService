package com.productcatalogservice.service.impl;

import com.productcatalogservice.entity.Role;
import com.productcatalogservice.entity.User;
import com.productcatalogservice.exception.AccessDeniedException;
import com.productcatalogservice.service.CategoryService;
import com.productcatalogservice.service.ProductService;
import com.productcatalogservice.service.StatisticsService;
import com.productcatalogservice.service.UserService;
import com.productcatalogservice.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final AuthUtil authUtil;

    @Override
    public Map<String, Object> getStatistics(String token) {
        User adminUser = authUtil.getUserByToken(token);
        if (adminUser == null || !Role.ADMIN.equals(adminUser.getRole())) {
            throw new AccessDeniedException("Доступ запрещен. Требуется роль ADMIN");
        }

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", userService.showAllUser().size());
        statistics.put("totalProducts", productService.count());
        statistics.put("totalCategories", categoryService.findAll().size());
        statistics.put("activeSessions", authUtil.getActiveSessionsCount());

        log.info("Статистика получена администратором: {}", adminUser.getUserName());
        return statistics;
    }
}