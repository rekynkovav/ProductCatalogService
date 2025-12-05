package org.example.model.dto;

import java.time.LocalDateTime;

public class AuditRecord {
    private final Long id;
    private final Long userId;
    private final String username;
    private final String action;
    private final String clientIp;
    private final String status;
    private final Long executionTime;
    private final String errorMessage;
    private final LocalDateTime createdDate;

    public AuditRecord(Long id, Long userId, String username, String action, String clientIp,
                       String status, Long executionTime, String errorMessage, LocalDateTime createdDate) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.clientIp = clientIp;
        this.status = status;
        this.executionTime = executionTime;
        this.errorMessage = errorMessage;
        this.createdDate = createdDate;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getAction() { return action; }
    public String getClientIp() { return clientIp; }
    public String getStatus() { return status; }
    public Long getExecutionTime() { return executionTime; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedDate() { return createdDate; }

    @Override
    public String toString() {
        return String.format(
                "AuditRecord{id=%d, user=%s, action='%s', status='%s', time=%dms}",
                id, username, action, status, executionTime
        );
    }
}

