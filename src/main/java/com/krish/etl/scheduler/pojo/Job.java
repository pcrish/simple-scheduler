package com.krish.etl.scheduler.pojo;


import java.util.List;

public class Job {
    private long jobId;
    private String jobName;
    private String jobDescription;
    private String jobGroup;
    private Runnable jobAction;
    private boolean isActive;
    private List<JobSchedule> jobSchedules;
    private List<JobDependencies> jobDependencies;
    private String jobSLA;
    private String action;
    private boolean withDependency;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public Runnable getJobAction() {
        return jobAction;
    }

    public void setJobAction(Runnable jobAction) {
        this.jobAction = jobAction;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<JobSchedule> getJobSchedules() {
        return jobSchedules;
    }

    public void setJobSchedules(List<JobSchedule> jobSchedules) {
        this.jobSchedules = jobSchedules;
    }

    public List<JobDependencies> getJobDependencies() {
        return jobDependencies;
    }

    public void setJobDependencies(List<JobDependencies> jobDependencies) {
        this.jobDependencies = jobDependencies;
    }

    public String getJobSLA() {
        return jobSLA;
    }

    public void setJobSLA(String jobSLA) {
        this.jobSLA = jobSLA;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isWithDependency() {
        return withDependency;
    }

    public void setWithDependency(boolean withDependency) {
        this.withDependency = withDependency;
    }
}
