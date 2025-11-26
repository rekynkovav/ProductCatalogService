package org.example.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@Aspect
public class AuditAspect {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    @Before("execution(* org.example.servlet.*.*(..)) && args(.., request)")
    public void auditBeforeMethod(HttpServletRequest request) {
        String action = request.getMethod() + " " + request.getRequestURI();
        String user = request.getRemoteUser() != null ? request.getRemoteUser() : "anonymous";
        String clientInfo = request.getRemoteAddr();

        auditLogger.info("ACTION_STARTED | User: {} | Action: {} | Client: {} | Time: {}",
                user, action, clientInfo, LocalDateTime.now());
    }

    @AfterReturning(pointcut = "execution(* org.example.servlet.*.*(..))", returning = "result")
    public void auditAfterMethod(Object result) {
        auditLogger.info("ACTION_COMPLETED | Result: {} | Time: {}",
                result != null ? "SUCCESS" : "NO_RESULT", LocalDateTime.now());
    }
}
