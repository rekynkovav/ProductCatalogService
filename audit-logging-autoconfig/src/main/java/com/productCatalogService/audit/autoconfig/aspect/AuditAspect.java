package com.productCatalogService.audit.autoconfig.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "product-catalog.audit",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class AuditAspect {

    private final ObjectMapper objectMapper;
    private static final String NO_HTTP_CONTEXT = "No HTTP Context";

    @Pointcut("execution(* com.productCatalogService..repository..*.save*(..))")
    public void saveMethods() {}

    @Pointcut("execution(* com.productCatalogService..repository..*.update*(..))")
    public void updateMethods() {}

    @Pointcut("execution(* com.productCatalogService..repository..*.delete*(..))")
    public void deleteMethods() {}

    @AfterReturning(
            pointcut = "saveMethods() || updateMethods() || deleteMethods()",
            returning = "result"
    )
    public void auditOperation(JoinPoint joinPoint, Object result) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String entityType = getEntityType(joinPoint, result);

            // Используем рефлексию для безопасного получения HTTP контекста
            Optional<HttpContextInfo> httpContext = getHttpContextInfo();

            String ipAddress = httpContext.map(HttpContextInfo::getIpAddress).orElse("Unknown");
            String requestUri = httpContext.map(HttpContextInfo::getRequestUri).orElse("Unknown");
            String userInfo = httpContext.map(HttpContextInfo::getUserInfo).orElse(NO_HTTP_CONTEXT);

            log.info("AUDIT: {} operation on {} from IP: {}, URL: {}, User: {}",
                    methodName, entityType, ipAddress, requestUri, userInfo);

            if (log.isDebugEnabled() && result != null) {
                try {
                    String entityJson = objectMapper.writeValueAsString(result);
                    log.debug("AUDIT DETAILS: {}", entityJson);
                } catch (Exception e) {
                    log.debug("AUDIT DETAILS: Unable to serialize entity: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to audit operation: {}", e.getMessage());
        }
    }

    @AfterThrowing(
            pointcut = "saveMethods() || updateMethods() || deleteMethods()",
            throwing = "exception"
    )
    public void auditFailedOperation(JoinPoint joinPoint, Exception exception) {
        try {
            String methodName = joinPoint.getSignature().getName();
            String entityType = getEntityType(joinPoint, null);

            Optional<HttpContextInfo> httpContext = getHttpContextInfo();
            String ipAddress = httpContext.map(HttpContextInfo::getIpAddress).orElse("Unknown");
            String userInfo = httpContext.map(HttpContextInfo::getUserInfo).orElse(NO_HTTP_CONTEXT);

            log.error("AUDIT FAILURE: {} operation on {} failed. Exception: {}, IP: {}, User: {}",
                    methodName, entityType, exception.getMessage(), ipAddress, userInfo);
        } catch (Exception e) {
            log.warn("Failed to audit failed operation: {}", e.getMessage());
        }
    }

    private String getEntityType(JoinPoint joinPoint, Object result) {
        try {
            if (result != null) {
                return result.getClass().getSimpleName();
            } else if (joinPoint.getArgs().length > 0 && joinPoint.getArgs()[0] != null) {
                return joinPoint.getArgs()[0].getClass().getSimpleName();
            } else {
                return "Unknown";
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private Optional<HttpContextInfo> getHttpContextInfo() {
        try {
            Class<?> requestContextHolder = Class.forName(
                    "org.springframework.web.context.request.RequestContextHolder");
            Class<?> servletRequestAttributes = Class.forName(
                    "org.springframework.web.context.request.ServletRequestAttributes");

            Object requestAttributes = requestContextHolder
                    .getMethod("getRequestAttributes")
                    .invoke(null);

            if (requestAttributes != null && servletRequestAttributes.isInstance(requestAttributes)) {
                Object request = servletRequestAttributes
                        .getMethod("getRequest")
                        .invoke(requestAttributes);

                if (request != null) {
                    Class<?> httpServletRequest = Class.forName("jakarta.servlet.http.HttpServletRequest");
                    String ipAddress = (String) httpServletRequest
                            .getMethod("getRemoteAddr")
                            .invoke(request);
                    String requestUri = (String) httpServletRequest
                            .getMethod("getRequestURI")
                            .invoke(request);
                    String remoteUser = (String) httpServletRequest
                            .getMethod("getRemoteUser")
                            .invoke(request);

                    return Optional.of(new HttpContextInfo(
                            ipAddress,
                            requestUri,
                            remoteUser != null ? remoteUser : "Anonymous"
                    ));
                }
            }
        } catch (ClassNotFoundException e) {
            log.debug("Servlet API not available: {}", e.getMessage());
        } catch (Exception e) {
            log.debug("Failed to get HTTP context: {}", e.getMessage());
        }

        return Optional.empty();
    }

    private static class HttpContextInfo {
        private final String ipAddress;
        private final String requestUri;
        private final String userInfo;

        public HttpContextInfo(String ipAddress, String requestUri, String userInfo) {
            this.ipAddress = ipAddress;
            this.requestUri = requestUri;
            this.userInfo = userInfo;
        }

        public String getIpAddress() { return ipAddress; }
        public String getRequestUri() { return requestUri; }
        public String getUserInfo() { return userInfo; }
    }
}