package com.smartbite.dto;

import java.time.Instant;

public class HealthResponse {

    private String status;
    private String message;
    private String timestamp;
    private String version;
    private String service;

    public HealthResponse() {
    }

    public HealthResponse(String status, String message, String version, String service) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now().toString();
        this.version = version;
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
}
