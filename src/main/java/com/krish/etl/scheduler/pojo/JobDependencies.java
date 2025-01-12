package com.krish.etl.scheduler.pojo;

public class JobDependencies {
    private int processId;
    private boolean isActive;

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
