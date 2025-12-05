package org.example.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.model.entity.User;
import org.example.repository.AspectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Аспект для аудита действий пользователей в сервлетах API.
 * Записывает информацию о выполнении HTTP-запросов, включая пользователя, действие,
 * IP-адрес, статус выполнения и время выполнения.
 */
@Aspect
public class AuditAspect {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private final AspectRepository aspectRepository;

    public AuditAspect(AspectRepository aspectRepository) {
        this.aspectRepository = aspectRepository;
    }

    @Around("execution(* org.example.servlet.ApiServlet.*(..)) && args(.., request, response)")
    public Object auditServletMethods(ProceedingJoinPoint joinPoint, HttpServletRequest request,
                                      HttpServletResponse response) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String action = request.getMethod() + " " + request.getRequestURI();

        User user = (User) request.getSession().getAttribute("user");
        Long userId = user != null ? user.getId() : null;
        String username = user != null ? user.getUserName() : "anonymous";
        String clientIp = getClientIp(request);

        String status = "SUCCESS";
        String errorMessage = null;
        Object result = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            status = "ERROR";
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            aspectRepository.saveAuditLog(
                    userId, username, action, clientIp, status,
                    executionTime, errorMessage
            );
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}