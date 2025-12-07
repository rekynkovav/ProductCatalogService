package org.example.audit.autoconfig.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Аспект для аудита операций изменения данных.
 * Автоматически логирует операции создания, обновления и удаления сущностей.
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see Aspect
 * @see Component
 */
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

    /**
     * Pointcut для методов сохранения сущностей.
     */
    @Pointcut("execution(* com.productcatalogservice..repository..*.save*(..))")
    public void saveMethods() {}

    /**
     * Pointcut для методов обновления сущностей.
     */
    @Pointcut("execution(* com.productcatalogservice..repository..*.update*(..))")
    public void updateMethods() {}

    /**
     * Pointcut для методов удаления сущностей.
     */
    @Pointcut("execution(* com.productcatalogservice..repository..*.delete*(..))")
    public void deleteMethods() {}

    /**
     * Логирует успешное выполнение операций изменения данных.
     *
     * @param joinPoint точка соединения AOP
     * @param result результат выполнения метода
     */
    @AfterReturning(
            pointcut = "saveMethods() || updateMethods() || deleteMethods()",
            returning = "result"
    )
    public void auditOperation(JoinPoint joinPoint, Object result) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getRequest();

            String methodName = joinPoint.getSignature().getName();
            String entityType = result != null ?
                    result.getClass().getSimpleName() :
                    joinPoint.getArgs()[0].getClass().getSimpleName();

            log.info("AUDIT: {} operation on {} from IP: {}, URL: {}",
                    methodName, entityType,
                    request.getRemoteAddr(), request.getRequestURI());

            if (log.isDebugEnabled()) {
                String entityJson = objectMapper.writeValueAsString(result);
                log.debug("AUDIT DETAILS: {}", entityJson);
            }
        } catch (Exception e) {
            log.warn("Failed to audit operation: {}", e.getMessage());
        }
    }

    /**
     * Логирует неудачные операции изменения данных.
     *
     * @param joinPoint точка соединения AOP
     * @param exception выброшенное исключение
     */
    @AfterThrowing(
            pointcut = "saveMethods() || updateMethods() || deleteMethods()",
            throwing = "exception"
    )
    public void auditFailedOperation(JoinPoint joinPoint, Exception exception) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes()).getRequest();

            String methodName = joinPoint.getSignature().getName();
            Object entity = joinPoint.getArgs().length > 0 ? joinPoint.getArgs()[0] : null;
            String entityType = entity != null ? entity.getClass().getSimpleName() : "Unknown";

            log.error("AUDIT FAILURE: {} operation on {} failed. Exception: {}, IP: {}",
                    methodName, entityType, exception.getMessage(), request.getRemoteAddr());
        } catch (Exception e) {
            log.warn("Failed to audit failed operation: {}", e.getMessage());
        }
    }
}