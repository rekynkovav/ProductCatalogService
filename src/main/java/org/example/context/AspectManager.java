package org.example.context;

import org.example.aspect.AuditAspect;
import org.example.aspect.LoggingAspect;

/**
 * Менеджер для инициализации и управления аспектами
 */
public class AspectManager {

    private static AspectManager instance;
    private LoggingAspect loggingAspect;
    private AuditAspect auditAspect;

    private AspectManager() {
        initializeAspects();
    }

    public static AspectManager getInstance() {
        if (instance == null) {
            instance = new AspectManager();
        }
        return instance;
    }

    private void initializeAspects() {
        this.loggingAspect = ApplicationContext.getInstance().getBean(LoggingAspect.class);
        this.auditAspect = ApplicationContext.getInstance().getBean(AuditAspect.class);
    }

    public LoggingAspect getLoggingAspect() {
        return loggingAspect;
    }

    public AuditAspect getAuditAspect() {
        return auditAspect;
    }
}