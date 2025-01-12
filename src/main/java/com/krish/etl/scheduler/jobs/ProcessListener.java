package com.krish.etl.scheduler.jobs;

import com.krish.etl.scheduler.dao.JobStatusDao;
import com.krish.etl.scheduler.dao.ProcessListenerDao;
import com.krish.etl.scheduler.pojo.DbJob;
import com.krish.etl.scheduler.pojo.Health;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

public class ProcessListener extends TimerTask {
    private final Logger logger = LoggerFactory.getLogger(ProcessListener.class);
    private final Health health = new Health();
    private final ProcessListenerDao processListenerDao;
    private final RunJob runJob;
    private final JobStatusDao jobStatusDao;

    public ProcessListener(ProcessListenerDao processListenerDao, RunJob runJob, JobStatusDao jobStatusDao) {
        this.processListenerDao = processListenerDao;
        this.runJob = runJob;
        this.jobStatusDao = jobStatusDao;
    }

    public void runProcessListener() throws SQLException {
        processListenerDao.runProcessListener();
    }
    @Override
    public void run() {
        try {
            logger.info("Process Listener started");
            runProcessListener();
            health.setAvailable(true);
            health.setAction("No action required");
            health.setMessage("Running fine");
            logger.info("Process Listener completed");
            List<DbJob> dbJobs = jobStatusDao.getReadyProcesses();
            for (DbJob dbJob : dbJobs) {
                runJob.startJob(dbJob);
            }
        } catch (Exception e) {
            logger.error("Error in Process Listener", e);
            health.setAvailable(false);
            health.setAction("Please check log to check error for Process Listener");
            health.setMessage("Source file monitor unhealthy");

        }
        health.setLastChecked(new Date());
    }


    public Health getHealth() {
        return health;
    }
}
