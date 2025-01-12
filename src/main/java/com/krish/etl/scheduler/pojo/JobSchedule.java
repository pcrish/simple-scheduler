package com.krish.etl.scheduler.pojo;

import java.util.List;

public class JobSchedule {
    private ScheduleTypes scheduleType;
    private List<String> processDays;
    private int dayOfMonth;
    private int month;
    private String time;

    public ScheduleTypes getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleTypes scheduleType) {
        this.scheduleType = scheduleType;
    }

    public List<String> getProcessDays() {
        return processDays;
    }

    public void setProcessDays(List<String> processDays) {
        this.processDays = processDays;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
