package com.krish.etl.scheduler.pojo;

import java.util.Date;

public class Health {
    private boolean isAvailable;
    private String healthCheck;
    private String message;
    private String action;
    private Date lastChecked;
    private String error;

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getHealthCheck() {
        return healthCheck;
    }

    public void setHealthCheck(String healthCheck) {
        this.healthCheck = healthCheck;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }


    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
