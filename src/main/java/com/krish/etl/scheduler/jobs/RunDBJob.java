package com.krish.etl.scheduler.jobs;

import com.krish.etl.scheduler.Job;
import com.krish.etl.scheduler.dao.RunDbJobDao;
import com.krish.etl.scheduler.pojo.DbJob;
import com.krish.etl.scheduler.util.ConnectionManager;

import java.util.HashMap;

public class RunDBJob implements Job {

    public RunDBJob() {
    }

    private RunDbJobDao runJobDao;
    private String parameter;
    private HashMap<String, Object> objectPool;


    @Override
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public void setOjbectPool(HashMap<String, Object> ojbectPool) {
        this.objectPool = ojbectPool;
    }

    @Override
    public void run(DbJob dbJob) throws Exception {
        runJobDao = new RunDbJobDao((ConnectionManager) objectPool.get("pg_db_connection_manager"));
        runJobDao.runJob(dbJob);
    }
}
