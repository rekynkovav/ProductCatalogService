package org.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.model.entity.User;
import org.example.service.UserService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Аспект для сбора метрик пользовательской активности.
 * Отслеживает различные операции: вход в систему, действия с корзиной,
 * управление товарами (добавление, обновление, удаление).
 * Предоставляет методы для получения собранных метрик в различных форматах.
 */
@Aspect
public class MetricsAspect {

    private final Map<Long, Map<String, Integer>> userMetrics = new ConcurrentHashMap<>();
    private final UserService userService;

    public MetricsAspect(UserService userService) {
        this.userService = userService;
    }

    @Pointcut("execution(* org.example.service.SecurityService.verificationUser(..))")
    public void loginOperation() {}

    @Pointcut("execution(* org.example.service.ProductService.addBasket(..))")
    public void addToBasketOperation() {}

    @Pointcut("execution(* org.example.service.ProductService.saveProduct(..))")
    public void addProductOperation() {}

    @Pointcut("execution(* org.example.service.ProductService.updateProduct(..))")
    public void updateProductOperation() {}

    @Pointcut("execution(* org.example.service.ProductService.deleteProductById(..))")
    public void deleteProductOperation() {}

    @Pointcut("execution(* org.example.servlet.ApiServlet.handleLogin(..)) && args(.., requestBody)")
    public void logoutOperation() {}

    @Around("loginOperation()")
    public Object trackLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        if (result instanceof Boolean && (Boolean) result) {
            String username = (String) joinPoint.getArgs()[0];
            Optional<User> optionalUser = userService.findByUsername(username);
            if (optionalUser.isPresent()) {
                incrementMetric(optionalUser.get().getId(), "LOGIN_COUNT");
            }
        }

        return result;
    }

    @Around("addToBasketOperation()")
    public Object trackAddToBasket(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        Long userId = (Long) joinPoint.getArgs()[0];
        incrementMetric(userId, "BASKET_ADD_COUNT");
        return result;
    }

    @Around("addProductOperation()")
    public Object trackAddProduct(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        incrementMetric(1L, "PRODUCT_ADD_COUNT");
        return result;
    }

    @Around("updateProductOperation()")
    public Object trackUpdateProduct(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        incrementMetric(1L, "PRODUCT_UPDATE_COUNT");
        return result;
    }

    @Around("deleteProductOperation()")
    public Object trackDeleteProduct(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        incrementMetric(1L, "PRODUCT_DELETE_COUNT");
        return result;
    }

    private void incrementMetric(Long userId, String metricName) {
        userMetrics.computeIfAbsent(userId, k -> new HashMap<>())
                .merge(metricName, 1, Integer::sum);
    }

    public Map<Long, Map<String, Integer>> getMetrics() {
        return new HashMap<>(userMetrics);
    }

    public String getFormattedMetrics() {
        StringBuilder sb = new StringBuilder();
        sb.append("User Metrics:\n");
        userMetrics.forEach((userId, metrics) -> {
            sb.append("User ID: ").append(userId).append("\n");
            metrics.forEach((metric, count) -> {
                sb.append("  ").append(metric).append(": ").append(count).append("\n");
            });
        });
        return sb.toString();
    }
}