package com.krish.etl.scheduler.api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.krish.etl.scheduler.dao.JobStatusDao;
import com.krish.etl.scheduler.pojo.DbJob;
import com.krish.etl.scheduler.util.ConnectionManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/jobs")

public class JobApi {
    private final Logger log = LoggerFactory.getLogger(JobApi.class);
    private final JobStatusDao jobStatusDao;
    private JobStatusDao getJobStatusDao;
    private ConnectionManager connectionManager;
    public JobApi(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.jobStatusDao = new JobStatusDao(connectionManager);
    }


    @GET
    @Produces("application/json")
    public Response getJobs(
                            @QueryParam("jobId") long jobId,
                            @QueryParam("orderDate") String orderDate,
                            @QueryParam("jobName")String jobName,
                            @QueryParam("jobTree")String jobTree,
                            @QueryParam("jobStatus") String jobStatus,
                            @QueryParam("statusList") String  statusList
                        ) {
        try {


            List<String> statusses = new ArrayList<>();
            if(!(statusList == null || statusList.isBlank()))
                for(String status : statusList.split(","))
                    statusses.add(status);

        List<DbJob> dbJobList = jobStatusDao.getJobs(jobId, orderDate, jobName, jobTree, jobStatus, statusses);
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").setPrettyPrinting().create();
        return Response.ok().entity(gson.toJson(dbJobList)).build();
        } catch (Exception e) {
            log.error("Exception occurred while getting job detail",e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response updateJob(DbJob dbJob) {
        try {
            jobStatusDao.updateStatusManually(dbJob);
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Exception occurred while updating status",e);
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
