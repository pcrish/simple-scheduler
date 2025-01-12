package com.krish.etl.scheduler.pojo;

import java.util.Map;

public class JobExecutionGroups {
    private String groupName;
    Map<Integer, Job> priorityMap;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Map<Integer, Job> getPriorityMap() {
        return priorityMap;
    }

    public void setPriorityMap(Map<Integer, Job> priorityMap) {
        this.priorityMap = priorityMap;
    }
}
