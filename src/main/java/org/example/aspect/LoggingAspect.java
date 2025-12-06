package org.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Аспект для логирования выполнения методов сервисного слоя.
 * <p>
 * Использует Spring AOP для перехвата вызовов методов в пакете {@code org.example.service}
 * и логирования информации о их выполнении.
 * </p>
 *
 * <h3>Что логируется:</h3>
 * <ul>
 *   <li>Начало выполнения метода (имя метода и аргументы)</li>
 *   <li>Успешное завершение метода</li>
 *   <li>Исключения, возникшие при выполнении метода</li>
 * </ul>
 *
 * @see Aspect
 * @see Component
 * @since 1.0
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Логирует начало выполнения метода сервиса.
     * <p>
     * Вызывается перед выполнением любого метода в пакете {@code org.example.service}
     * и его подпакетах.
     *
     * @param joinPoint точка соединения, содержащая информацию о выполняемом методе
     * @see Before
     * @see JoinPoint
     */
    @Before("execution(* org.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Executing method: {} with arguments: {}",
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Логирует успешное завершение метода сервиса.
     * <p>
     * Вызывается после успешного выполнения любого метода в пакете {@code org.example.service}.
     * Имеет доступ к возвращаемому значению метода.
     *
     * @param joinPoint точка соединения с информацией о выполненном методе
     * @param result возвращаемое значение метода (может быть {@code null})
     * @see AfterReturning
     */
    @AfterReturning(pointcut = "execution(* org.example.service.*.*(..))",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} executed successfully", joinPoint.getSignature().getName());
    }

    /**
     * Логирует исключение, возникшее при выполнении метода сервиса.
     * <p>
     * Вызывается при возникновении любого неперехваченного исключения в методах
     * пакета {@code org.example.service}.
     *
     * @param joinPoint точка соединения с информацией о методе
     * @param error исключение, которое было выброшено
     * @see AfterThrowing
     */
    @AfterThrowing(pointcut = "execution(* org.example.service.*.*(..))",
            throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in method: {} with cause: {}",
                joinPoint.getSignature().getName(), error.getMessage());
    }
}