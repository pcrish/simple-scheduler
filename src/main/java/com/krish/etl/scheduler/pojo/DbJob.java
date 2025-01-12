package com.krish.etl.scheduler.pojo;

import java.util.Date;

public class DbJob {
    private int jobId;
    private Date orderDate;
    private String stdOut;
    private String error;
    private String fullyQualifiedClassName;
    private String parameter;
    private String executableType;

    // api variables

    private String name;
    private String description;
    private String tree;
    private String status;
    private String statusDescription;
    private String started;
    private String completed;
    private int retry;
    private String startAt;
    private String sla;
    private String scheduleType;
    private String jobDays;
    private String nthDay;
    private String nthWorkingDay;


    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStdOut() {
        return stdOut;
    }

    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getExecutableType() {
        return executableType;
    }

    public void setExecutableType(String executableType) {
        this.executableType = executableType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTree() {
        return tree;
    }

    public void setTree(String tree) {
        this.tree = tree;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getSla() {
        return sla;
    }

    public void setSla(String sla) {
        this.sla = sla;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getJobDays() {
        return jobDays;
    }

    public void setJobDays(String jobDays) {
        this.jobDays = jobDays;
    }

    public String getNthDay() {
        return nthDay;
    }

    public void setNthDay(String nthDay) {
        this.nthDay = nthDay;
    }

    public String getNthWorkingDay() {
        return nthWorkingDay;
    }

    public void setNthWorkingDay(String nthWorkingDay) {
        this.nthWorkingDay = nthWorkingDay;
    }
}
