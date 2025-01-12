package com.krish.etl.scheduler;


import com.krish.etl.scheduler.dao.JobStatusDao;
import com.krish.etl.scheduler.dao.ProcessListenerDao;
import com.krish.etl.scheduler.dao.SourceFileDao;
import com.krish.etl.scheduler.dao.SourceFileMonitorDao;
import com.krish.etl.scheduler.jobs.ProcessListener;
import com.krish.etl.scheduler.jobs.RunJob;
import com.krish.etl.scheduler.jobs.SourceFilesMonitor;
import com.krish.etl.scheduler.pojo.Health;
import com.krish.etl.scheduler.util.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;

public class SimpleScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleScheduler.class);
    private final ConnectionManager connectionManager;
    private final HashMap<String, Object> objectPool;
    private JobStatusDao jobStatusDao;
    private SourceFilesMonitor sourceFileMonitor;
    private ProcessListener processListener;
    private RunJob runJob;
    private final Timer timer;
    private final long schedulerCoreJobDelay = 20 * 1000;

    public SimpleScheduler(Builder builder) {
        this.connectionManager = builder.connectionManager;
        this.objectPool = builder.objectPool;
        timer = new Timer();
    }



    public void runSourceFileMonitror() {
        SourceFileDao sourceFileDao = new SourceFileDao(connectionManager);
        SourceFileMonitorDao sourceFileMonitorDao = new SourceFileMonitorDao(connectionManager);
        sourceFileMonitor = new SourceFilesMonitor(sourceFileDao, sourceFileMonitorDao);
        timer.schedule(sourceFileMonitor, schedulerCoreJobDelay,schedulerCoreJobDelay);
        LOG.info("Source file monitor started to run after every {} ms",schedulerCoreJobDelay);
    }

    public void runProcessListener() {
        ProcessListenerDao processListenerDao = new ProcessListenerDao(connectionManager);
        jobStatusDao = new JobStatusDao(connectionManager);
        this.runJob = new RunJob(jobStatusDao,objectPool);
        processListener = new ProcessListener(processListenerDao, runJob, jobStatusDao);
        timer.schedule(processListener, schedulerCoreJobDelay,schedulerCoreJobDelay);
        LOG.info("Process Listener started to run after every {} ms",schedulerCoreJobDelay);
    }

    public void createOnDemandJobs() {
        LOG.info("Creating on demand jobs");
        LOG.info("Created on demand jobs");
    }

    public HashMap<String, Health> getHealth() {
        HashMap<String, Health> health = new HashMap<>();
        health.put("SchedulerSourceFileMonitor", sourceFileMonitor.getHealth());
        health.put("SchedulerProcessListener", processListener.getHealth());
        health.put("SchedulerRunJob", runJob.getHealth());
        health.putAll(jobStatusDao.getJobHealth());
        return health;
    }

    public void stop() {
        LOG.info("Stopping scheduler");
        sourceFileMonitor.cancel();
        processListener.cancel();
        timer.cancel();
        LOG.info("Scheduler stopped");
    }

    public static Builder newScheduler(ConnectionManager connectionManager) {
        return new Builder(connectionManager);
    }

    public static class Builder {
        private final HashMap<String, Object> objectPool = new HashMap<>();
        private final ConnectionManager connectionManager;

        public Builder(ConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }


        public Builder withObject(String name, Object object) {
            objectPool.put(name, object);
            return this;
        }
        public SimpleScheduler schedule() {
            if(connectionManager == null){
                LOG.error("ConnectionManager is required");
                throw new RuntimeException("ConnectionManager is required");
            }
            return new SimpleScheduler(this);
        }
    }



}
