package com.productcatalogservice.audit.autoconfig.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

/**
 * Аспект для автоматического логирования выполнения методов в приложении.
 * Автоматически подключается при наличии зависимости в classpath и включенной настройке.
 *
 * <p>Логирует:</p>
 * <ul>
 *   <li>Время выполнения методов</li>
 *   <li>Входные параметры и возвращаемые значения</li>
 *   <li>Исключения, возникающие при выполнении</li>
 * </ul>
 *
 * @author Product Catalog Service Team
 * @since 1.0.0
 * @see Aspect
 * @see Component
 */
@Aspect
@Component
@Slf4j
@ConditionalOnProperty(
        prefix = "product-catalog.audit.logging",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class LoggingAspect {

    /**
     * Pointcut для всех методов в пакете service.
     */
    @Pointcut("execution(* com.productcatalogservice..service..*.*(..))")
    public void serviceMethods() {}

    /**
     * Pointcut для всех методов в пакете controller.
     */
    @Pointcut("execution(* com.productcatalogservice..controller..*.*(..))")
    public void controllerMethods() {}

    /**
     * Pointcut для всех методов в пакете repository.
     */
    @Pointcut("execution(* com.productcatalogservice..repository..*.*(..))")
    public void repositoryMethods() {}

    /**
     * Перехватывает выполнение методов для логирования входа/выхода и времени выполнения.
     *
     * @param joinPoint точка соединения AOP
     * @return результат выполнения метода
     * @throws Throwable если метод выбросил исключение
     */
    @Around("serviceMethods() || controllerMethods() || repositoryMethods()")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) {
            return joinPoint.proceed();
        }

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.debug("Entering method [{}.{}] with arguments: {}",
                className, methodName, Arrays.toString(args));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();

            log.debug("Exiting method [{}.{}] with result: {} (execution time: {} ms)",
                    className, methodName, result, stopWatch.getTotalTimeMillis());

            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("Exception in method [{}.{}] after {} ms: {}",
                    className, methodName, stopWatch.getTotalTimeMillis(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Логирует исключения, выброшенные методами.
     *
     * @param joinPoint точка соединения AOP
     * @param exception выброшенное исключение
     */
    @AfterThrowing(
            pointcut = "serviceMethods() || controllerMethods() || repositoryMethods()",
            throwing = "exception"
    )
    public void logException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("Exception in [{}.{}]: {}", className, methodName, exception.getMessage(), exception);
    }
}