package com.krish.etl.scheduler.jobs;

import com.krish.etl.scheduler.Job;
import com.krish.etl.scheduler.dao.JobStatusDao;
import com.krish.etl.scheduler.pojo.DbJob;
import com.krish.etl.scheduler.pojo.Health;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;

public class RunJob {
    private final JobStatusDao jobStatusDao;
    private final HashMap<String,Object> objectPool;
    private final Health health = new Health();
    public RunJob(JobStatusDao jobStatusDao, HashMap<String, Object> objectPool) {
        this.jobStatusDao = jobStatusDao;
        this.objectPool = objectPool;
    }

    public Health getHealth() {
        return health;
    }

    public void startJob(DbJob dbJob){
        Thread thread = new Thread(() -> {
            try {
            String error;
            jobStatusDao.markStarted(dbJob);

            Class aClass = Class.forName(dbJob.getFullyQualifiedClassName());
            Object object =  aClass.getDeclaredConstructor().newInstance();
            Job job = (Job) object;
            job.setParameter(dbJob.getParameter());
            job.setOjbectPool(objectPool);
            job.run(dbJob);
            if(!dbJob.getExecutableType().equals("pg_db"))
                jobStatusDao.markSuccess(dbJob);
            } catch (Exception e) {
                try {
                    StringWriter stringWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stringWriter));
                    dbJob.setError(stringWriter.toString());
                    //if(!dbJob.getExecutableType().equals("pg_db") || e instanceof ClassNotFoundException) //this is not required
                        jobStatusDao.markFailed(dbJob);
                } catch (SQLException ex) {
                    health.setAvailable(false);
                    health.setAction("Check db connection");
                    health.setMessage(String.format("Upable to update status for job %s", dbJob.getJobId()));
                }
            }
        });
        thread.start();
    }

}
