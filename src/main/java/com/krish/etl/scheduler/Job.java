package com.krish.etl.scheduler;

import com.krish.etl.scheduler.pojo.DbJob;

import java.util.HashMap;

public interface Job {
    void setParameter(String parameter);
    void setOjbectPool(HashMap<String,Object> ojbectPool) ;
    void run(DbJob dbJob) throws Exception;
}
